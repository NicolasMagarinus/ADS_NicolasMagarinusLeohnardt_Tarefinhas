package br.upf.todolist.controller;

import br.upf.todolist.facade.UsuarioFacade;
import br.upf.todolist.util.Mensagens;
import br.upf.todolist.util.RegraNegocioException;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class CadastroController {

    @EJB
    private UsuarioFacade usuarioFacade;

    @Inject
    private LoginController loginController;

    private String nome;
    private String email;
    private String senha;
    private String confirmacao;

    public String cadastrar() {
        try {
            usuarioFacade.cadastrar(nome, email, senha, confirmacao);
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
            return null;
        }
        Mensagens.sucesso("Conta criada. Bem-vindo(a), " + nome.trim() + "!");
        return loginController.autenticar(email, senha);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmacao() {
        return confirmacao;
    }

    public void setConfirmacao(String confirmacao) {
        this.confirmacao = confirmacao;
    }
}
