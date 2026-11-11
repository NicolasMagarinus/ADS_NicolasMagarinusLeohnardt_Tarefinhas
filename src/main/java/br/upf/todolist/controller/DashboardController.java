package br.upf.todolist.controller;

import br.upf.todolist.entity.StatusTarefa;
import br.upf.todolist.entity.Tarefa;
import br.upf.todolist.facade.TarefaFacade;
import br.upf.todolist.facade.TarefaFiltro;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DashboardController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private TarefaFacade tarefaFacade;

    @Inject
    private UsuarioLogado usuarioLogado;

    private long aFazer;
    private long fazendo;
    private long concluidas;
    private long atrasadas;
    private List<Tarefa> tarefasRecentes;

    @PostConstruct
    public void carregar() {
        aFazer = contar(StatusTarefa.A_FAZER, false);
        fazendo = contar(StatusTarefa.FAZENDO, false);
        concluidas = contar(StatusTarefa.CONCLUIDA, false);
        atrasadas = contar(null, true);
        tarefasRecentes = tarefaFacade.buscar(new TarefaFiltro(), usuarioLogado.getUsuario(), 0, 5, null, false);
    }

    private long contar(StatusTarefa status, boolean soAtrasadas) {
        TarefaFiltro filtro = new TarefaFiltro();
        filtro.setStatus(status);
        filtro.setSoAtrasadas(soAtrasadas);
        return tarefaFacade.contar(filtro, usuarioLogado.getUsuario());
    }

    public long getTarefasAFazer() { return aFazer; }
    public long getFazendo() { return fazendo; }
    public long getConcluidas() { return concluidas; }
    public long getAtrasadas() { return atrasadas; }
    public List<Tarefa> getTarefasRecentes() { return tarefasRecentes; }
}
