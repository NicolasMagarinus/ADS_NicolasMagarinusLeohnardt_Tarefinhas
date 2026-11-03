package br.upf.todolist.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "subtarefa")
public class Subtarefa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;
    @NotBlank @Size(max = 150) @Column(name = "descricao", nullable = false, length = 150)
    private String descricao;
    @Column(name = "concluida", nullable = false) private boolean concluida;
    @Column(name = "ordem", nullable = false) private int ordem;
    public Long getId() { return id; }
    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }
}
