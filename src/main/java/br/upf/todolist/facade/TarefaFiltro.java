package br.upf.todolist.facade;

import br.upf.todolist.entity.Prioridade;
import br.upf.todolist.entity.StatusTarefa;
import java.io.Serializable;

public class TarefaFiltro implements Serializable {

    private static final long serialVersionUID = 1L;

    private String texto;
    private StatusTarefa status;
    private Prioridade prioridade;
    private Long categoriaId;
    private boolean soAtrasadas;

    public void limpar() {
        texto = null;
        status = null;
        prioridade = null;
        categoriaId = null;
        soAtrasadas = false;
    }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }
    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public boolean isSoAtrasadas() { return soAtrasadas; }
    public void setSoAtrasadas(boolean soAtrasadas) { this.soAtrasadas = soAtrasadas; }
}
