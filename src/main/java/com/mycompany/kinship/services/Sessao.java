package com.mycompany.kinship.services;

import com.mycompany.kinship.models.Usuario;

public final class Sessao {

    private static Usuario usuarioLogado;

    private Sessao() {}

    public static void iniciar(Usuario usuario) { usuarioLogado = usuario; }
    public static Usuario getUsuarioLogado() { return usuarioLogado; }
    public static boolean estaLogado() { return usuarioLogado != null; }
    public static void encerrar() { usuarioLogado = null; }
}