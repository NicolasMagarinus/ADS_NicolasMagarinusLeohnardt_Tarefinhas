package br.upf.todolist.facade;

import br.upf.todolist.entity.Categoria;
import br.upf.todolist.entity.Usuario;
import br.upf.todolist.util.RegraNegocioException;
import br.upf.todolist.util.Textos;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Stateless
public class CategoriaFacade extends AbstractFacade<Categoria> {

    private static final Pattern COR_HEX = Pattern.compile("#?[0-9a-fA-F]{6}");

    @PersistenceContext(unitName = "todolistPU")
    private EntityManager em;

    public CategoriaFacade() {
        super(Categoria.class);
    }

    CategoriaFacade(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Categoria> listarDoUsuario(Usuario usuario) {
        return em.createQuery("SELECT c FROM Categoria c WHERE c.usuario.id = :usuarioId ORDER BY c.nome", Categoria.class)
                .setParameter("usuarioId", usuario.getId())
                .getResultList();
    }

    public Categoria buscarDoUsuario(Long id, Usuario usuario) {
        Categoria categoria = find(id);
        if (categoria == null || categoria.getUsuario() == null
                || !categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraNegocioException("Categoria não encontrada.");
        }
        return categoria;
    }

    public Categoria salvar(Categoria dados, Usuario usuario) {
        String nome = Textos.obrigatorio(dados.getNome(), 50,
                "Informe o nome da categoria.", "O nome da categoria deve ter no máximo 50 caracteres.");
        String cor = normalizarCor(dados.getCor());

        if (dados.getId() == null) {
            validarNomeUnico(nome, null, usuario);
            Categoria nova = new Categoria();
            nova.setNome(nome);
            nova.setCor(cor);
            nova.setUsuario(referencia(Usuario.class, usuario.getId()));
            create(nova);
            return nova;
        }

        Categoria existente = buscarDoUsuario(dados.getId(), usuario);
        validarNomeUnico(nome, existente.getId(), usuario);
        existente.setNome(nome);
        existente.setCor(cor);
        return existente;
    }

    public void excluir(Long id, Usuario usuario) {
        Categoria categoria = buscarDoUsuario(id, usuario);
        em.createQuery("UPDATE Tarefa t SET t.categoria = NULL WHERE t.categoria = :categoria")
                .setParameter("categoria", categoria)
                .executeUpdate();
        remove(categoria);
    }

    public static String normalizarCor(String cor) {
        String valor = cor == null ? "" : cor.trim();
        if (!COR_HEX.matcher(valor).matches()) {
            throw new RegraNegocioException("Cor inválida.");
        }
        return (valor.startsWith("#") ? valor : "#" + valor).toLowerCase(Locale.ROOT);
    }

    private void validarNomeUnico(String nome, Long idAtual, Usuario usuario) {
        Long total = em.createQuery(
                "SELECT COUNT(c) FROM Categoria c WHERE c.usuario.id = :usuarioId"
                + " AND LOWER(c.nome) = LOWER(:nome) AND c.id <> :idAtual", Long.class)
                .setParameter("usuarioId", usuario.getId())
                .setParameter("nome", nome)
                .setParameter("idAtual", idAtual == null ? -1L : idAtual)
                .getSingleResult();
        if (total > 0) {
            throw new RegraNegocioException("Você já tem uma categoria com esse nome.");
        }
    }
}
