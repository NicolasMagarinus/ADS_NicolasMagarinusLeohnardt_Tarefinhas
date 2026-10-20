package br.upf.todolist.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

/**
 * Mensagens globais exibidas no p:growl do template. Em requisições normais
 * (não Ajax) as mensagens sobrevivem a um redirect (flash), então podem ser
 * usadas antes de "?faces-redirect=true". Em Ajax não há redirect neste
 * sistema, então o flash não é usado.
 */
public final class Mensagens {

    private Mensagens() {
    }

    public static void sucesso(String texto) {
        adicionar(FacesMessage.SEVERITY_INFO, texto);
    }

    public static void erro(String texto) {
        adicionar(FacesMessage.SEVERITY_ERROR, texto);
    }

    private static void adicionar(FacesMessage.Severity severidade, String texto) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        contexto.addMessage(null, new FacesMessage(severidade, texto, null));
        if (!contexto.getPartialViewContext().isAjaxRequest()) {
            contexto.getExternalContext().getFlash().setKeepMessages(true);
        }
    }
}
