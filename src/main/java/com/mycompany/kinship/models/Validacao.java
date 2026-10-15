package com.mycompany.kinship.models;

final class Validacao {

    private Validacao() {
    }

    static String obrigatorio(String valor, String campo, int max) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        String limpo = valor.trim();
        if (limpo.length() > max) {
            throw new IllegalArgumentException(campo + " deve ter no máximo " + max + " caracteres.");
        }
        return limpo;
    }

    static String opcional(String valor, String campo, int max) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String limpo = valor.trim();
        if (limpo.length() > max) {
            throw new IllegalArgumentException(campo + " deve ter no máximo " + max + " caracteres.");
        }
        return limpo;
    }

    static String email(String valor, int max) {
        String limpo = obrigatorio(valor, "E-mail", max);
        if (!limpo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
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