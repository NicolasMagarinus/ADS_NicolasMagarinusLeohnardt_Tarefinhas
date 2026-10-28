package br.upf.todolist.entity;

public enum StatusTarefa {
    A_FAZER("A fazer"),
    FAZENDO("Fazendo"),
    CONCLUIDA("Concluída");

    private final String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
