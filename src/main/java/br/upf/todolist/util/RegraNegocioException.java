package br.upf.todolist.util;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
