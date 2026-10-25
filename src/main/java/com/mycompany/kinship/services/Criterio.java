package com.mycompany.kinship.services;

import com.mycompany.kinship.models.Post;
import java.util.Comparator;

public enum Criterio {

    CURTIDAS((a, b) -> {
        int c = Integer.compare(b.getTotalCurtidas(), a.getTotalCurtidas());
        return c != 0 ? c : Integer.compare(b.getCodigo(), a.getCodigo());
    }),
    COMENTARIOS((a, b) -> {
        int c = Integer.compare(b.getTotalComentarios(), a.getTotalComentarios());
        return c != 0 ? c : Integer.compare(b.getCodigo(), a.getCodigo());
    }),
    DATA((a, b) -> {
        int c = b.getDataPublicacao().compareTo(a.getDataPublicacao());
        return c != 0 ? c : Integer.compare(b.getCodigo(), a.getCodigo());
    });

    private final Comparator<Post> comparador;

    Criterio(Comparator<Post> comparador) {
        this.comparador = comparador;
    }

    public Comparator<Post> comparador() {
        return comparador;
    }
}