package br.upf.todolist.facade;

import br.upf.todolist.entity.Usuario;
import br.upf.todolist.util.Emails;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

/**
 * Acesso a dados do usuário. Nesta etapa só existe o que o login precisa:
 * buscar o usuário pelo e-mail (usado pelo JpaIdentityStore e pelo UsuarioLogado).
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "todolistPU")
    private EntityManager em;

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
}
