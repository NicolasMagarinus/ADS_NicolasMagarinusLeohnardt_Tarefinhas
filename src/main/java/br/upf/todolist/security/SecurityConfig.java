package br.upf.todolist.security;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;

/**
 * Configuração do Jakarta Security: login por formulário próprio (login.xhtml).
 * Ao acessar uma página protegida sem estar logado, o usuário é redirecionado
 * para o login e, depois de autenticar, volta para a página que tinha pedido.
 */
@CustomFormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/login.xhtml",
                errorPage = "",
                useForwardToLogin = false))
@DeclareRoles({"USER", "ADMIN"})
@ApplicationScoped
public class SecurityConfig {
}
