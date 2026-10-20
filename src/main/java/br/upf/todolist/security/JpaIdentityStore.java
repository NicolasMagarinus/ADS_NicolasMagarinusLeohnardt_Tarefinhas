package br.upf.todolist.security;

import br.upf.todolist.entity.Usuario;
import br.upf.todolist.facade.UsuarioFacade;
import br.upf.todolist.util.Emails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.Optional;
import java.util.Set;

/**
 * Valida e-mail e senha contra a tabela "usuario". Chamado pelo Jakarta
 * Security quando o LoginController executa securityContext.authenticate(...).
 *
 * Devolve os grupos USER (todos) e ADMIN (administradores), que o
 * glassfish-web.xml mapeia para os papéis usados no web.xml.
 */
@ApplicationScoped
public class JpaIdentityStore implements IdentityStore {

    @Inject
    private UsuarioFacade usuarioFacade;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    public JpaIdentityStore() {
    }

    /** Usado nos testes unitários. */
    JpaIdentityStore(UsuarioFacade usuarioFacade, Pbkdf2PasswordHash passwordHash) {
        this.usuarioFacade = usuarioFacade;
        this.passwordHash = passwordHash;
    }

    public CredentialValidationResult validate(UsernamePasswordCredential credencial) {
        String email = Emails.normalizar(credencial.getCaller());
        Optional<Usuario> encontrado = usuarioFacade.buscarPorEmail(email);
        if (encontrado.isEmpty()) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        Usuario usuario = encontrado.get();
        boolean senhaConfere = passwordHash.verify(credencial.getPassword().getValue(), usuario.getSenhaHash());
        if (!senhaConfere || !usuario.isAtivo()) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        Set<String> grupos = usuario.isAdmin() ? Set.of("USER", "ADMIN") : Set.of("USER");
        return new CredentialValidationResult(usuario.getEmail(), grupos);
    }
}
