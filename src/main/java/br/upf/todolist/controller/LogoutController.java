package br.upf.todolist.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Named
@RequestScoped
public class LogoutController {

    public String sair() throws ServletException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ((HttpServletRequest) ec.getRequest()).logout();
        ec.invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }
}
