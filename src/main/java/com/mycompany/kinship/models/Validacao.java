package com.mycompany.kinship.models;

import java.util.Locale;
import java.util.regex.Pattern;

final class Validacao {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern NICKNAME = Pattern.compile("^[A-Za-z0-9_.]{3,30}$");

    private Validacao() {
    }

    static String obrigatorio(String valor, String campo, int max) {
        return obrigatorio(null, valor, campo, max);
    }

    static String opcional(String valor, String campo, int max) {
        return opcional(null, valor, campo, max);
    }

    static String email(String valor, int max) {
        return email(null, valor, max);
    }

    static String obrigatorio(String chave, String valor, String rotulo, int max) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DominioException(chave, rotulo + " é obrigatório.");
        }
        String limpo = valor.trim();
        if (limpo.length() > max) {
            throw new DominioException(chave, rotulo + " deve ter no máximo " + max + " caracteres.");
        }
        return limpo;
    }

    static String opcional(String chave, String valor, String rotulo, int max) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String limpo = valor.trim();
        if (limpo.length() > max) {
            throw new DominioException(chave, rotulo + " deve ter no máximo " + max + " caracteres.");
        }
        return limpo;
    }

    static String email(String chave, String valor, int max) {
        String limpo = obrigatorio(chave, valor, "E-mail", max).toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(limpo).matches()) {
            throw new DominioException(chave, "E-mail inválido.");
        }
        return limpo;
    }

    static String nickname(String chave, String valor) {
        String limpo = obrigatorio(chave, valor, "Nickname", 30);
        if (!NICKNAME.matcher(limpo).matches()) {
            throw new DominioException(chave,
                    "Nickname: 3 a 30 caracteres, só letras, números, ponto ou _.");
        }
        return limpo;
    }

    static int idPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " inválido.");
        }
        return valor;
    }

    static int naoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(campo + " não pode ser negativo.");
        }
        return valor;
    }
}