package br.upf.todolist.facade;

import br.upf.todolist.entity.Categoria;
import br.upf.todolist.entity.Prioridade;
import br.upf.todolist.entity.StatusTarefa;
import br.upf.todolist.entity.Tarefa;
import br.upf.todolist.entity.Usuario;
import br.upf.todolist.util.RegraNegocioException;
import br.upf.todolist.util.Textos;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Stateless
public class TarefaFacade extends AbstractFacade<Tarefa> {

    private static final String NAO_ENCONTRADA = "Tarefa não encontrada.";
    private static final String SO_DONO = "Apenas o dono da tarefa pode fazer isso.";
    private static final Set<String> CAMPOS_ORDENAVEIS = Set.of("titulo", "prioridade", "dataVencimento", "status", "criadoEm");

    @PersistenceContext(unitName = "todolistPU")
    private EntityManager em;

    public TarefaFacade() {
        super(Tarefa.class);
    }

    TarefaFacade(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Tarefa buscarVisivel(Long id, Usuario usuario) {
        Tarefa tarefa = find(id);
        if (tarefa == null || !tarefa.podeVer(usuario)) {
            throw new RegraNegocioException(NAO_ENCONTRADA);
        }
        return tarefa;
    }

    public Tarefa salvar(Tarefa dados, Usuario usuario) {
        if (dados.getId() == null) {
            Tarefa nova = new Tarefa();
            copiarCamposEditaveis(dados, nova, usuario);
            nova.setUsuario(referencia(Usuario.class, usuario.getId()));
            nova.setStatus(StatusTarefa.A_FAZER);
            nova.setCriadoEm(OffsetDateTime.now());
            nova.setPosicao(proximaPosicao(usuario.getId(), StatusTarefa.A_FAZER));
            create(nova);
            return nova;
        }

        Tarefa existente = buscarVisivel(dados.getId(), usuario);
        if (!existente.podeEditar(usuario)) {
            throw new RegraNegocioException(SO_DONO);
        }
        copiarCamposEditaveis(dados, existente, existente.getUsuario());
        aplicarStatus(existente, dados.getStatus());
        return existente;
    }

    public void mudarStatus(Long id, StatusTarefa novoStatus, Usuario usuario) {
        Tarefa tarefa = buscarVisivel(id, usuario);
        if (!tarefa.podeMudarStatus(usuario)) {
            throw new RegraNegocioException(SO_DONO);
        }
        aplicarStatus(tarefa, novoStatus);
    }

    public void excluir(Long id, Usuario usuario) {
        Tarefa tarefa = buscarVisivel(id, usuario);
        if (!tarefa.podeEditar(usuario)) {
            throw new RegraNegocioException(SO_DONO);
        }
        remove(tarefa);
    }

    public List<Tarefa> buscar(TarefaFiltro filtro, Usuario usuario, int primeiro, int quantidade,
            String campoOrdem, boolean crescente) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tarefa> consulta = cb.createQuery(Tarefa.class);
        Root<Tarefa> tarefa = consulta.from(Tarefa.class);
        consulta.select(tarefa).where(condicoes(cb, tarefa, filtro, usuario));
        consulta.orderBy(ordenacao(cb, tarefa, campoOrdem, crescente));
        return em.createQuery(consulta).setFirstResult(primeiro).setMaxResults(quantidade).getResultList();
    }

    public long contar(TarefaFiltro filtro, Usuario usuario) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> consulta = cb.createQuery(Long.class);
        Root<Tarefa> tarefa = consulta.from(Tarefa.class);
        consulta.select(cb.count(tarefa)).where(condicoes(cb, tarefa, filtro, usuario));
        return em.createQuery(consulta).getSingleResult();
    }

    private Predicate[] condicoes(CriteriaBuilder cb, Root<Tarefa> tarefa, TarefaFiltro filtro, Usuario usuario) {
        List<Predicate> predicados = new ArrayList<>();
        predicados.add(cb.equal(tarefa.get("usuario").get("id"), usuario.getId()));

        if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
            String padrao = "%" + filtro.getTexto().trim().toLowerCase(Locale.ROOT) + "%";
            predicados.add(cb.or(
                    cb.like(cb.lower(tarefa.get("titulo")), padrao),
                    cb.like(cb.lower(tarefa.get("descricao")), padrao)));
        }
        if (filtro.getStatus() != null) predicados.add(cb.equal(tarefa.get("status"), filtro.getStatus()));
        if (filtro.getPrioridade() != null) predicados.add(cb.equal(tarefa.get("prioridade"), filtro.getPrioridade()));
        if (filtro.getCategoriaId() != null) {
            predicados.add(cb.equal(tarefa.get("categoria").get("id"), filtro.getCategoriaId()));
        }
        if (filtro.isSoAtrasadas()) {
            predicados.add(cb.lessThan(tarefa.get("dataVencimento"), LocalDate.now()));
            predicados.add(cb.notEqual(tarefa.get("status"), StatusTarefa.CONCLUIDA));
        }
        return predicados.toArray(Predicate[]::new);
    }

    private List<Order> ordenacao(CriteriaBuilder cb, Root<Tarefa> tarefa, String campo, boolean crescente) {
        String campoSeguro = campo != null && CAMPOS_ORDENAVEIS.contains(campo) ? campo : "criadoEm";
        boolean ascendente = campoSeguro.equals("criadoEm") && campo == null ? false : crescente;
        Order ordem = ascendente ? cb.asc(tarefa.get(campoSeguro)) : cb.desc(tarefa.get(campoSeguro));
        return List.of(ordem, cb.asc(tarefa.get("id")));
    }

    private void copiarCamposEditaveis(Tarefa origem, Tarefa destino, Usuario dono) {
        destino.setTitulo(Textos.obrigatorio(origem.getTitulo(), 150,
                "Informe o título da tarefa.", "O título deve ter no máximo 150 caracteres."));
        destino.setDescricao(origem.getDescricao());
        destino.setPrioridade(origem.getPrioridade() == null ? Prioridade.MEDIA : origem.getPrioridade());
        destino.setDataVencimento(origem.getDataVencimento());
        destino.setCategoria(categoriaDoDono(origem.getCategoria(), dono));
    }

    private Categoria categoriaDoDono(Categoria categoria, Usuario dono) {
        if (categoria == null || categoria.getId() == null) return null;
        Categoria doBanco = em.find(Categoria.class, categoria.getId());
        if (doBanco == null || doBanco.getUsuario() == null || !doBanco.getUsuario().getId().equals(dono.getId())) {
            throw new RegraNegocioException("Categoria não encontrada.");
        }
        return doBanco;
    }

    private void aplicarStatus(Tarefa tarefa, StatusTarefa novoStatus) {
        if (novoStatus == null || novoStatus == tarefa.getStatus()) return;
        tarefa.setPosicao(proximaPosicao(tarefa.getUsuario().getId(), novoStatus));
        tarefa.mudarStatus(novoStatus, OffsetDateTime.now());
    }

    private int proximaPosicao(Long usuarioId, StatusTarefa status) {
        Integer maior = em.createQuery(
                "SELECT MAX(t.posicao) FROM Tarefa t WHERE t.usuario.id = :usuarioId AND t.status = :status", Integer.class)
                .setParameter("usuarioId", usuarioId)
                .setParameter("status", status)
                .getSingleResult();
        return maior == null ? 1 : maior + 1;
    }
}
