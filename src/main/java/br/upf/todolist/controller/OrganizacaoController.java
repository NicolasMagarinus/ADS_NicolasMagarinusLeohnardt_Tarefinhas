package br.upf.todolist.controller;

import br.upf.todolist.entity.Categoria;
import br.upf.todolist.facade.CategoriaFacade;
import br.upf.todolist.util.Mensagens;
import br.upf.todolist.util.RegraNegocioException;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named
@ViewScoped
public class OrganizacaoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private CategoriaFacade categoriaFacade;

    @Inject
    private UsuarioLogado usuarioLogado;

    private List<Categoria> categorias;
    private Categoria categoria = new Categoria();

    @PostConstruct
    public void init() {
        carregarCategorias();
    }

    public void novaCategoria() {
        categoria = new Categoria();
    }

    public void editarCategoria(Categoria selecionada) {
        categoria = new Categoria();
        categoria.setId(selecionada.getId());
        categoria.setNome(selecionada.getNome());
        categoria.setCor(selecionada.getCor());
    }

    public void salvarCategoria() {
        try {
            boolean nova = categoria.getId() == null;
            categoriaFacade.salvar(categoria, usuarioLogado.getUsuario());
            Mensagens.sucesso(nova ? "Categoria criada." : "Categoria atualizada.");
            carregarCategorias();
            PrimeFaces.current().ajax().addCallbackParam("salvo", true);
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
        }
    }

    public void excluirCategoria(Categoria selecionada) {
        try {
            categoriaFacade.excluir(selecionada.getId(), usuarioLogado.getUsuario());
            Mensagens.sucesso("Categoria excluída. As tarefas dela ficaram sem categoria.");
            carregarCategorias();
        } catch (RegraNegocioException e) {
            Mensagens.erro(e.getMessage());
        }
    }

    private void carregarCategorias() {
        categorias = categoriaFacade.listarDoUsuario(usuarioLogado.getUsuario());
    }

    public List<Categoria> getCategorias() { return categorias; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
