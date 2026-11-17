package br.upf.todolist.controller;

import br.upf.todolist.facade.UsuarioFacade;
import br.upf.todolist.util.Mensagens;
import br.upf.todolist.util.RegraNegocioException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class PerfilController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UsuarioFacade usuarioFacade;

    @Inject
    private UsuarioLogado usuarioLogado;

    private String nome;
    private String senhaAtual;
    private String novaSenha;
    private String confirmacao;

    @PostConstruct
    public void init() {
        nome = usuarioLogado.getUsuario().getNome();
    }

    public void salvar() {
        try {
            var usuario = usuarioFacade.atualizarPerfil(usuarioLogado.getUsuario(), nome,
                    senhaAtual, novaSenha, confirmacao);
            usuarioLogado.recarregar();
            nome = usuario.getNome();
            limparSenhas();
            Mensagens.sucesso("Perfil atualizado.");
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
            limparSenhas();
        }
    }

    private void limparSenhas() {
        senhaAtual = null;
        novaSenha = null;
        confirmacao = null;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSenhaAtual() { return senhaAtual; }
    public void setSenhaAtual(String senhaAtual) { this.senhaAtual = senhaAtual; }
    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
    public String getConfirmacao() { return confirmacao; }
    public void setConfirmacao(String confirmacao) { this.confirmacao = confirmacao; }
}
