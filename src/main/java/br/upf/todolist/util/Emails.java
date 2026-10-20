package br.upf.todolist.util;

import java.util.Locale;

public final class Emails {

    private Emails() {
    }

    /** E-mails são comparados e gravados sem espaços e em minúsculas. */
    public static String normalizar(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
