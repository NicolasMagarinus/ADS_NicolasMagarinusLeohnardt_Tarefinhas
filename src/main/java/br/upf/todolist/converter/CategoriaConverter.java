package br.upf.todolist.converter;

import br.upf.todolist.controller.UsuarioLogado;
import br.upf.todolist.entity.Categoria;
import br.upf.todolist.facade.CategoriaFacade;
import br.upf.todolist.util.RegraNegocioException;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@Dependent
@FacesConverter(forClass = Categoria.class, managed = true)
public class CategoriaConverter implements Converter<Categoria> {

    @Inject
    private CategoriaFacade categoriaFacade;

    @Inject
    private UsuarioLogado usuarioLogado;

    @Override
    public Categoria getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return categoriaFacade.buscarDoUsuario(Long.valueOf(value), usuarioLogado.getUsuario());
        } catch (NumberFormatException | RegraNegocioException e) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Categoria inválida.", null));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Categoria value) {
        return value == null || value.getId() == null ? "" : value.getId().toString();
    }
}
