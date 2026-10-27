package br.upf.todolist.controller;

import br.upf.todolist.entity.Usuario;
import br.upf.todolist.facade.UsuarioFacade;
import br.upf.todolist.util.Mensagens;
import br.upf.todolist.util.RegraNegocioException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AdminUsuariosController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UsuarioFacade usuarioFacade;

    @Inject
    private UsuarioLogado usuarioLogado;

    private List<Usuario> usuarios;

    @PostConstruct
    public void init() {
        carregar();
    }

    public void alternarAtivo(Usuario usuario) {
        boolean ativar = !usuario.isAtivo();
        try {
            usuarioFacade.alterarAtivo(usuarioLogado.getUsuario(), usuario.getId(), ativar);
            Mensagens.sucesso(usuario.getNome() + (ativar ? " foi desbloqueado(a)." : " foi bloqueado(a)."));
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
        }
        carregar();
    }

    public boolean euMesmo(Usuario usuario) {
        return usuario.getId().equals(usuarioLogado.getUsuario().getId());
    }

    private void carregar() {
        usuarios = usuarioFacade.listarTodos();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
