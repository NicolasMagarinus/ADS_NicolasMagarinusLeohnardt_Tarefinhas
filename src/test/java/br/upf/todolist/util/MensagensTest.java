package br.upf.todolist.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.context.PartialViewContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MensagensTest {

    /** FacesContext.setCurrentInstance é protegido; esta subclasse só dá acesso a ele. */
    private abstract static class ContextoFalso extends FacesContext {
        static void definir(FacesContext contexto) {
            setCurrentInstance(contexto);
        }
    }

    private FacesContext contexto;
    private PartialViewContext parcial;
    private Flash flash;

    @BeforeEach
    void setUp() {
        contexto = mock(FacesContext.class);
        ExternalContext externo = mock(ExternalContext.class);
        parcial = mock(PartialViewContext.class);
        flash = mock(Flash.class);
        when(contexto.getExternalContext()).thenReturn(externo);
        when(contexto.getPartialViewContext()).thenReturn(parcial);
        when(externo.getFlash()).thenReturn(flash);
        ContextoFalso.definir(contexto);
    }

    @AfterEach
    void tearDown() {
        ContextoFalso.definir(null);
    }

    @Test
    void requisicaoNormal_mantemMensagemParaDepoisDoRedirect() {
        when(parcial.isAjaxRequest()).thenReturn(false);

        Mensagens.sucesso("Tarefa criada.");

        verify(contexto).addMessage(isNull(), any());
        verify(flash).setKeepMessages(true);
    }

    /**
     * Em Ajax não há redirect; ligar o flash só gera o aviso JSF1095 quando a
     * resposta é grande (o cookie do flash chega depois da resposta enviada).
     */
    @Test
    void ajax_naoUsaFlash() {
        when(parcial.isAjaxRequest()).thenReturn(true);

        Mensagens.erro("Você não pode bloquear a si mesmo.");

        verify(contexto).addMessage(isNull(), any());
        verify(flash, never()).setKeepMessages(true);
    }
}
