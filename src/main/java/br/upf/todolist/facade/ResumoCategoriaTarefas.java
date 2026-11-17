package br.upf.todolist.facade;

import java.io.Serializable;

public record ResumoCategoriaTarefas(String categoria, Long quantidade) implements Serializable {
}
