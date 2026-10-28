package br.upf.todolist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "tarefa")
public class Tarefa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @NotBlank(message = "Informe o título da tarefa.")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusTarefa status = StatusTarefa.A_FAZER;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", nullable = false, length = 10)
    private Prioridade prioridade = Prioridade.MEDIA;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "posicao", nullable = false)
    private int posicao;

    @NotNull
    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "concluido_em")
    private OffsetDateTime concluidoEm;

    public void mudarStatus(StatusTarefa novo, OffsetDateTime agora) {
        if (novo == null || novo == status) return;
        status = novo;
        concluidoEm = novo == StatusTarefa.CONCLUIDA ? agora : null;
    }

    public boolean isAtrasada(LocalDate hoje) {
        return dataVencimento != null && dataVencimento.isBefore(hoje) && status != StatusTarefa.CONCLUIDA;
    }

    public boolean isAtrasada() { return isAtrasada(LocalDate.now()); }
    public boolean isConcluida() { return status == StatusTarefa.CONCLUIDA; }

    public boolean isDono(Usuario outro) {
        return outro != null && outro.getId() != null && usuario != null
                && usuario.getId() != null && outro.getId().equals(usuario.getId());
    }

    public boolean podeVer(Usuario outro) { return isDono(outro); }
    public boolean podeEditar(Usuario outro) { return isDono(outro); }
    public boolean podeMudarStatus(Usuario outro) { return isDono(outro); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }
    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public int getPosicao() { return posicao; }
    public void setPosicao(int posicao) { this.posicao = posicao; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(OffsetDateTime concluidoEm) { this.concluidoEm = concluidoEm; }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tarefa other)) return false;
        return id != null && id.equals(other.id);
    }
}
