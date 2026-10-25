package com.mycompany.kinship.services;

import java.util.Map;

public class ValidacaoException extends Exception {

    private final Map<String, String> erros;

    public ValidacaoException(Map<String, String> erros) {
        super("Dados inválidos");
        this.erros = erros;
    }

    public Map<String, String> getErros() {
        return erros;
    }
}