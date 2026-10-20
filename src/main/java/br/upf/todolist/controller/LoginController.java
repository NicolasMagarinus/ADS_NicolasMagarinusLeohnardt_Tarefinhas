package br.upf.todolist.controller;

import br.upf.todolist.util.Emails;
import br.upf.todolist.util.Mensagens;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Named
@RequestScoped
public class LoginController {

    /** Página aberta depois do login. */
    public static final String PAGINA_INICIAL = "/app/dashboard.xhtml";

    @Inject
    private SecurityContext securityContext;

    private String email;
    private String senha;

    /** f:viewAction da tela de login: quem já está logado vai direto para a aplicação. */
    public void verificarJaLogado() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        if (ec.getUserPrincipal() != null) {
            ec.redirect(ec.getRequestContextPath() + PAGINA_INICIAL);
        }
    }

    public String entrar() {
        return autenticar(email, senha);
    }

    /**
     * Autentica pelo Jakarta Security (que chama o JpaIdentityStore).
     * Também usado pelo cadastro, para entrar logo após criar a conta.
     */
    String autenticar(String emailInformado, String senhaInformada) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        ExternalContext ec = contexto.getExternalContext();

        UsernamePasswordCredential credencial = new UsernamePasswordCredential(
                Emails.normalizar(emailInformado), new Password(senhaInformada == null ? "" : senhaInformada));

        AuthenticationStatus status = securityContext.authenticate(
                (HttpServletRequest) ec.getRequest(),
                (HttpServletResponse) ec.getResponse(),
                // newAuthentication(false): se o usuário tinha tentado abrir uma página protegida,
                // o Jakarta Security o leva de volta para ela (com true, essa página seria esquecida)
                AuthenticationParameters.withParams().credential(credencial).newAuthentication(false));

        switch (status) {
            case SEND_CONTINUE:
                // O Jakarta Security já redirecionou para a página que o usuário tinha pedido
                contexto.responseComplete();
                return null;
            case SUCCESS:
                return PAGINA_INICIAL + "?faces-redirect=true";
            default:
                Mensagens.erro("E-mail ou senha inválidos.");
                return null;
        }
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
}
