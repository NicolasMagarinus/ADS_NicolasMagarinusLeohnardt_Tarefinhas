package br.upf.todolist.controller;

import br.upf.todolist.entity.Categoria;
import br.upf.todolist.entity.Prioridade;
import br.upf.todolist.entity.StatusTarefa;
import br.upf.todolist.entity.Tarefa;
import br.upf.todolist.facade.CategoriaFacade;
import br.upf.todolist.facade.TarefaFacade;
import br.upf.todolist.util.Mensagens;
import br.upf.todolist.util.RegraNegocioException;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class TarefaDetalheController implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String LISTA = "/app/tarefas?faces-redirect=true";

    @EJB
    private TarefaFacade tarefaFacade;

    @EJB
    private CategoriaFacade categoriaFacade;

    @Inject
    private UsuarioLogado usuarioLogado;

    private Long id;
    private Tarefa tarefa;
    private List<Categoria> categorias;

    public String carregar() {
        categorias = categoriaFacade.listarDoUsuario(usuarioLogado.getUsuario());
        if (id == null) {
            tarefa = new Tarefa();
            return null;
        }
        try {
            tarefa = tarefaFacade.buscarVisivel(id, usuarioLogado.getUsuario());
            return null;
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
            return LISTA;
        }
    }

    public String salvar() {
        try {
            boolean nova = isNova();
            tarefaFacade.salvar(tarefa, usuarioLogado.getUsuario());
            Mensagens.sucesso(nova ? "Tarefa criada." : "Tarefa atualizada.");
            return LISTA;
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
            return null;
        }
    }

    public boolean isNova() {
        return tarefa == null || tarefa.getId() == null;
    }

    public boolean isPodeEditar() {
        return isNova() || tarefa.podeEditar(usuarioLogado.getUsuario());
    }

    public StatusTarefa[] getStatusDisponiveis() { return StatusTarefa.values(); }
    public Prioridade[] getPrioridades() { return Prioridade.values(); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tarefa getTarefa() { return tarefa; }
    public List<Categoria> getCategorias() { return categorias; }
}
