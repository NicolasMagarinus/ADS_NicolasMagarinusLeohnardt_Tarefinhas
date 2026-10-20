package br.upf.todolist.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.upf.todolist.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsuarioFacadeTest {

    private TypedQuery<Usuario> consulta;
    private UsuarioFacade facade;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        EntityManager em = mock(EntityManager.class);
        consulta = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(anyString(), eq(Usuario.class))).thenReturn(consulta);
        facade = new UsuarioFacade(em);
    }

    @Test
    void buscarPorEmail_encontrado_retornaUsuario() {
        Usuario ana = new Usuario();
        ana.setEmail("ana@x.com");
        when(consulta.getResultList()).thenReturn(List.of(ana));

        Optional<Usuario> resultado = facade.buscarPorEmail("ana@x.com");

        assertThat(resultado).containsSame(ana);
    }

    @Test
    void buscarPorEmail_naoEncontrado_retornaVazio() {
        when(consulta.getResultList()).thenReturn(List.of());

        assertThat(facade.buscarPorEmail("ninguem@x.com")).isEmpty();
    }

    @Test
    void buscarPorEmail_normalizaAntesDeConsultar() {
        when(consulta.getResultList()).thenReturn(List.of());

        facade.buscarPorEmail("  ANA@X.com ");

        verify(consulta).setParameter("email", "ana@x.com");
    }
}
