package br.upf.todolist.controller;

import br.upf.todolist.entity.Usuario;
import br.upf.todolist.facade.UsuarioFacade;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.security.Principal;

/**
 * Guarda na sessão o usuário autenticado. O Jakarta Security só conhece o
 * e-mail (nome do "caller"); aqui carregamos a entidade Usuario uma única vez.
 */
@Named
@SessionScoped
public class UsuarioLogado implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UsuarioFacade usuarioFacade;

    private Usuario usuario;

    public Usuario getUsuario() {
        if (usuario == null) {
            Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            if (principal != null) {
                usuario = usuarioFacade.buscarPorEmail(principal.getName()).orElse(null);
            }
        }
        return usuario;
    }

    public boolean isAdmin() {
        Usuario u = getUsuario();
        return u != null && u.isAdmin();
    }

    /** Descarta o usuário em cache (por exemplo, depois de alterar o perfil). */
    public void recarregar() {
        usuario = null;
    }
}
