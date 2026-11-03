package br.upf.todolist.controller;

import br.upf.todolist.entity.Categoria;
import br.upf.todolist.entity.Prioridade;
import br.upf.todolist.entity.StatusTarefa;
import br.upf.todolist.entity.Tarefa;
import br.upf.todolist.entity.Subtarefa;
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
import java.util.ArrayList;

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
    private List<Subtarefa> subtarefas = new ArrayList<>();
    private String novaSubtarefa;

    public String carregar() {
        categorias = categoriaFacade.listarDoUsuario(usuarioLogado.getUsuario());
        if (id == null) {
            tarefa = new Tarefa();
            return null;
        }
        try {
            tarefa = tarefaFacade.buscarVisivel(id, usuarioLogado.getUsuario());
            recarregarSubtarefas();
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

    public void adicionarSubtarefa() {
        try {
            tarefaFacade.adicionarSubtarefa(tarefa.getId(), novaSubtarefa, usuarioLogado.getUsuario());
            novaSubtarefa = null;
            recarregarSubtarefas();
        } catch (RegraNegocioException e) { Mensagens.erro(e.getMessage()); }
    }
    public void marcar(Subtarefa s) {
        try { tarefaFacade.marcarSubtarefa(tarefa.getId(), s.getId(), s.isConcluida(), usuarioLogado.getUsuario()); }
        catch (RegraNegocioException e) { Mensagens.erro(e.getMessage()); }
        recarregarSubtarefas();
    }
    public void remover(Subtarefa s) {
        try { tarefaFacade.removerSubtarefa(tarefa.getId(), s.getId(), usuarioLogado.getUsuario()); }
        catch (RegraNegocioException e) { Mensagens.erro(e.getMessage()); }
        recarregarSubtarefas();
    }
    private void recarregarSubtarefas() { subtarefas = tarefaFacade.listarSubtarefas(tarefa.getId(), usuarioLogado.getUsuario()); }
    public int getProgresso() { return subtarefas.isEmpty() ? 0 : (int) subtarefas.stream().filter(Subtarefa::isConcluida).count() * 100 / subtarefas.size(); }
    public List<Subtarefa> getSubtarefas() { return subtarefas; }
    public String getNovaSubtarefa() { return novaSubtarefa; }
    public void setNovaSubtarefa(String novaSubtarefa) { this.novaSubtarefa = novaSubtarefa; }

    public StatusTarefa[] getStatusDisponiveis() { return StatusTarefa.values(); }
    public Prioridade[] getPrioridades() { return Prioridade.values(); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tarefa getTarefa() { return tarefa; }
    public List<Categoria> getCategorias() { return categorias; }
}
