package br.upf.todolist.controller;

import br.upf.todolist.entity.Tarefa;
import br.upf.todolist.entity.Usuario;
import br.upf.todolist.facade.TarefaFacade;
import br.upf.todolist.facade.TarefaFiltro;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

public class TarefaLazyDataModel extends LazyDataModel<Tarefa> {

    private static final long serialVersionUID = 1L;

    private final TarefaFacade tarefaFacade;
    private final TarefaFiltro filtro;
    private final Usuario usuario;

    public TarefaLazyDataModel(TarefaFacade tarefaFacade, TarefaFiltro filtro, Usuario usuario) {
        this.tarefaFacade = tarefaFacade;
        this.filtro = filtro;
        this.usuario = usuario;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) tarefaFacade.contar(filtro, usuario);
    }

    @Override
    public List<Tarefa> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        String campo = null;
        boolean crescente = true;
        if (sortBy != null && !sortBy.isEmpty()) {
            SortMeta ordem = sortBy.values().iterator().next();
            campo = ordem.getField();
            crescente = ordem.getOrder() != SortOrder.DESCENDING;
        }
        return tarefaFacade.buscar(filtro, usuario, first, pageSize, campo, crescente);
    }

    @Override
    public String getRowKey(Tarefa tarefa) {
        return String.valueOf(tarefa.getId());
    }

    @Override
    public Tarefa getRowData(String rowKey) {
        List<Tarefa> pagina = getWrappedData();
        if (pagina == null) return null;
        return pagina.stream().filter(t -> String.valueOf(t.getId()).equals(rowKey)).findFirst().orElse(null);
    }
}
