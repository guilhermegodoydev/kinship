package com.mycompany.kinship.models;

public class DominioException extends IllegalArgumentException {

    private final String campo;

    public DominioException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo == null ? "geral" : campo;
    }

    public String getCampo() {
        return campo;
    }
}