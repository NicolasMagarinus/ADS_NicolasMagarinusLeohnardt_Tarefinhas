package br.upf.todolist.facade;

import br.upf.todolist.entity.Papel;
import br.upf.todolist.entity.Usuario;
import br.upf.todolist.util.Emails;
import br.upf.todolist.util.RegraNegocioException;
import br.upf.todolist.util.Textos;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.regex.Pattern;


@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    static final int SENHA_MIN = 8;
    static final int SENHA_MAX = 100;
    private static final Pattern FORMATO_EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    @PersistenceContext(unitName = "todolistPU")
    private EntityManager em;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    public UsuarioFacade() {
        super(Usuario.class);
    }

    /** Usado nos testes unitários. */
    UsuarioFacade(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", Emails.normalizar(email))
                .getResultList()
                .stream()
                .findFirst();
    }

    public Usuario cadastrar(String nome, String email, String senha, String confirmacao) {
        String nomeLimpo = Textos.obrigatorio(nome, 100,
                "Informe o nome.", "O nome deve ter no máximo 100 caracteres.");
        String emailNormalizado = Emails.normalizar(email);
        if (emailNormalizado.length() > 150 || !FORMATO_EMAIL.matcher(emailNormalizado).matches()) {
            throw new RegraNegocioException("E-mail inválido.");
        }
        validarNovaSenha(senha, confirmacao);
        if (existeEmail(emailNormalizado)) {
            throw new RegraNegocioException("Este e-mail já está cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nomeLimpo);
        usuario.setEmail(emailNormalizado);
        usuario.setSenhaHash(passwordHash.generate(senha.toCharArray()));
        usuario.setPapel(Papel.USER);
        usuario.setAtivo(true);
        usuario.setCriadoEm(OffsetDateTime.now());
        create(usuario);
        return usuario;
    }

    private boolean existeEmail(String emailNormalizado) {
        Long total = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", Long.class)
                .setParameter("email", emailNormalizado)
                .getSingleResult();
        return total > 0;
    }

    private static void validarNovaSenha(String senha, String confirmacao) {
        if (senha == null || senha.length() < SENHA_MIN || senha.length() > SENHA_MAX) {
            throw new RegraNegocioException("A senha deve ter entre 8 e 100 caracteres.");
        }
        if (!senha.equals(confirmacao)) {
            throw new RegraNegocioException("A confirmação não confere com a senha.");
        }
    }
}
