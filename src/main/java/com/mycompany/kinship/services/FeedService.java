package com.mycompany.kinship.services;

import com.mycompany.kinship.algoritmos.Algoritmo;
import com.mycompany.kinship.algoritmos.Metricas;
import com.mycompany.kinship.dao.PostDAO;
import com.mycompany.kinship.models.Post;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class FeedService {

    public static final int LIMITE_ORDENACAO = 10_000;

    public record Carga(List<Post> posts, int totalNoBanco) {}

    private final PostDAO postDAO = new PostDAO();

    public Carga carregar() {
        try {
            int total = postDAO.contar();
            List<Post> posts = postDAO.listarParaFeed(LIMITE_ORDENACAO);
            return new Carga(posts, total);
        } catch (SQLException e) {
            throw new ErroDeAcessoException("Falha ao carregar o feed.", e);
        }
    }

    public ResultadoOrdenacao ordenar(List<Post> originais, Criterio criterio, Algoritmo algoritmo) {
        Post[] array = originais.toArray(new Post[0]);

        long inicio = System.nanoTime();
        Metricas metricas = algoritmo.ordenar(array, criterio.comparador());
        long nanos = System.nanoTime() - inicio;

        return new ResultadoOrdenacao(Arrays.asList(array), nanos,
                metricas.getComparacoes(), metricas.getTrocas());
    }
}