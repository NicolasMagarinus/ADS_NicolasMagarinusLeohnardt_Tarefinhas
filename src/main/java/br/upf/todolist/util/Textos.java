package br.upf.todolist.util;

public final class Textos {

    private Textos() {
    }

    public static String obrigatorio(String valor, int maximo, String mensagemVazio, String mensagemTamanho) {
        String limpo = valor == null ? "" : valor.trim();
        if (limpo.isEmpty()) {
            throw new RegraNegocioException(mensagemVazio);
        }
        if (limpo.length() > maximo) {
            throw new RegraNegocioException(mensagemTamanho);
        }
        return limpo;
    }
}
