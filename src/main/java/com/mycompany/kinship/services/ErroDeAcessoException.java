package com.mycompany.kinship.services;

public class ErroDeAcessoException extends RuntimeException {

    public ErroDeAcessoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}