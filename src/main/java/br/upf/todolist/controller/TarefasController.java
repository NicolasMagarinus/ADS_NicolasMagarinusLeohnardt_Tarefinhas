package br.upf.todolist.controller;

import br.upf.todolist.entity.Categoria;
import br.upf.todolist.entity.Prioridade;
import br.upf.todolist.entity.StatusTarefa;
import br.upf.todolist.entity.Tarefa;
import br.upf.todolist.facade.CategoriaFacade;
import br.upf.todolist.facade.TarefaFacade;
import br.upf.todolist.facade.TarefaFiltro;
import br.upf.todolist.util.Mensagens;
import br.upf.todolist.util.RegraNegocioException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.component.datatable.DataTable;

@Named
@ViewScoped
public class TarefasController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String TABELA = ":frmTarefas:tblTarefas";

    @EJB
    private TarefaFacade tarefaFacade;

    @EJB
    private CategoriaFacade categoriaFacade;

    @Inject
    private UsuarioLogado usuarioLogado;

    private final TarefaFiltro filtro = new TarefaFiltro();
    private TarefaLazyDataModel modelo;
    private List<Categoria> categorias;

    @PostConstruct
    public void init() {
        modelo = new TarefaLazyDataModel(tarefaFacade, filtro, usuarioLogado.getUsuario());
        categorias = categoriaFacade.listarDoUsuario(usuarioLogado.getUsuario());
    }

    public void filtrar() {
        DataTable tabela = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(TABELA);
        if (tabela != null) tabela.setFirst(0);
    }

    public void limparFiltros() {
        filtro.limpar();
        filtrar();
    }

    public void concluir(Tarefa tarefa) {
        alterarStatus(tarefa, StatusTarefa.CONCLUIDA, "Tarefa concluída.");
    }

    public void reabrir(Tarefa tarefa) {
        alterarStatus(tarefa, StatusTarefa.A_FAZER, "Tarefa reaberta.");
    }

    public void excluir(Tarefa tarefa) {
        try {
            tarefaFacade.excluir(tarefa.getId(), usuarioLogado.getUsuario());
            Mensagens.sucesso("Tarefa excluída.");
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
        }
    }

    private void alterarStatus(Tarefa tarefa, StatusTarefa status, String mensagem) {
        try {
            tarefaFacade.mudarStatus(tarefa.getId(), status, usuarioLogado.getUsuario());
            Mensagens.sucesso(mensagem);
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
        }
    }

    public StatusTarefa[] getStatusDisponiveis() { return StatusTarefa.values(); }
    public Prioridade[] getPrioridades() { return Prioridade.values(); }
    public TarefaFiltro getFiltro() { return filtro; }
    public TarefaLazyDataModel getModelo() { return modelo; }
    public List<Categoria> getCategorias() { return categorias; }
}
