package br.upf.todolist.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.upf.todolist.entity.Papel;
import br.upf.todolist.entity.Usuario;
import br.upf.todolist.facade.UsuarioFacade;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.CredentialValidationResult.Status;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JpaIdentityStoreTest {

    private UsuarioFacade usuarioFacade;
    private Pbkdf2PasswordHash hash;
    private JpaIdentityStore store;

    @BeforeEach
    void setUp() {
        usuarioFacade = mock(UsuarioFacade.class);
        hash = mock(Pbkdf2PasswordHash.class);
        when(usuarioFacade.buscarPorEmail(anyString())).thenReturn(Optional.empty());
        // A senha correta de todos os usuários de teste é "certa"
        when(hash.verify(any(), eq("HASH"))).thenAnswer(inv ->
                Arrays.equals((char[]) inv.getArgument(0), "certa".toCharArray()));
        store = new JpaIdentityStore(usuarioFacade, hash);
    }

    private void cadastrado(String email, Papel papel, boolean ativo) {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setEmail(email);
        u.setSenhaHash("HASH");
        u.setPapel(papel);
        u.setAtivo(ativo);
        when(usuarioFacade.buscarPorEmail(email)).thenReturn(Optional.of(u));
    }

    @Test
    void validate_credencialValida_retornaGruposUser() {
        cadastrado("ana@x.com", Papel.USER, true);

        CredentialValidationResult r = store.validate(new UsernamePasswordCredential("ana@x.com", "certa"));

        assertThat(r.getStatus()).isEqualTo(Status.VALID);
        assertThat(r.getCallerPrincipal().getName()).isEqualTo("ana@x.com");
        assertThat(r.getCallerGroups()).containsExactly("USER");
    }

    @Test
    void validate_admin_retornaGruposUserEAdmin() {
        cadastrado("admin@x.com", Papel.ADMIN, true);

        CredentialValidationResult r = store.validate(new UsernamePasswordCredential("admin@x.com", "certa"));

        assertThat(r.getStatus()).isEqualTo(Status.VALID);
        assertThat(r.getCallerGroups()).containsExactlyInAnyOrder("USER", "ADMIN");
    }

    @Test
    void validate_senhaErrada_invalid() {
        cadastrado("ana@x.com", Papel.USER, true);

        CredentialValidationResult r = store.validate(new UsernamePasswordCredential("ana@x.com", "errada"));

        assertThat(r.getStatus()).isEqualTo(Status.INVALID);
    }

    @Test
    void validate_usuarioInexistente_invalid() {
        CredentialValidationResult r = store.validate(new UsernamePasswordCredential("ninguem@x.com", "certa"));

        assertThat(r.getStatus()).isEqualTo(Status.INVALID);
    }

    @Test
    void validate_usuarioBloqueado_invalid() {
        cadastrado("ana@x.com", Papel.USER, false);

        CredentialValidationResult r = store.validate(new UsernamePasswordCredential("ana@x.com", "certa"));

        assertThat(r.getStatus()).isEqualTo(Status.INVALID);
    }

    @Test
    void validate_emailComMaiusculasEEspacos_normaliza() {
        cadastrado("ana@x.com", Papel.USER, true);

        CredentialValidationResult r = store.validate(new UsernamePasswordCredential("  ANA@X.com ", "certa"));

        assertThat(r.getStatus()).isEqualTo(Status.VALID);
        assertThat(r.getCallerPrincipal().getName()).isEqualTo("ana@x.com");
    }
}
