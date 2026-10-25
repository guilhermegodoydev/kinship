
package com.mycompany.kinship.services;

import com.mycompany.kinship.algoritmos.Algoritmo;
import com.mycompany.kinship.algoritmos.Metricas;
import com.mycompany.kinship.models.Post;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import com.mycompany.kinship.dao.BenchmarkSeedDAO;
import com.mycompany.kinship.dao.PostDAO;
import java.sql.SQLException;
import java.util.concurrent.CancellationException;

public class BenchmarkService {

    private static final int REPETICOES = 3;
    private static final long SEED = 20261025L;

    public enum TipoDados {
        ALEATORIO,
        QUASE_ORDENADO,
        INVERSO
    }

    public int gerarDados(int quantidade) {
        try {
            return new BenchmarkSeedDAO().gerar(quantidade);
        } catch (SQLException e) {
            throw new ErroDeAcessoException(
                    "Falha ao gerar a massa de benchmark.", e);
        }
    }

    public void apagarDados() {
        try {
            new BenchmarkSeedDAO().apagar();
        } catch (SQLException e) {
            throw new ErroDeAcessoException(
                    "Falha ao apagar a massa de benchmark.", e);
        }
    }

    public int contarPosts() {
        try {
            return new PostDAO().contar();
        } catch (SQLException e) {
            throw new ErroDeAcessoException(
                    "Falha ao consultar a quantidade de posts.", e);
        }
    }

    public record Resultado(
            String algoritmo,
            long nanos,
            long comparacoes,
            long trocas) {
    }

    private void verificarCancelamento() {
        if (Thread.currentThread().isInterrupted()) {
            throw new CancellationException("Benchmark cancelado.");
        }
    }
    
    public List<Resultado> executar(
            List<Post> posts,
            Criterio criterio,
            TipoDados tipoDados) {

        if (posts == null || posts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Não há posts suficientes para executar o benchmark.");
        }

        if (criterio == null || tipoDados == null) {
            throw new IllegalArgumentException(
                    "Selecione o critério e o tipo de dados.");
        }

        Comparator<Post> comparadorOriginal = criterio.comparador();

        Comparator<Post> comparador = (a, b) -> {
            verificarCancelamento();
            return comparadorOriginal.compare(a, b);
        };
        
        List<Post> entrada = prepararEntrada(posts, comparador, tipoDados);
        List<Resultado> resultados = new ArrayList<>();

        
        for (Algoritmo algoritmo : Algoritmo.values()) {
            verificarCancelamento();

            Post[] aquecimento = entrada.toArray(new Post[0]);
            algoritmo.ordenar(aquecimento, comparador);

            long[] tempos = new long[REPETICOES];
            Metricas metricasFinais = null;

            for (int i = 0; i < REPETICOES; i++) {
                verificarCancelamento();

                Post[] copia = entrada.toArray(new Post[0]);
                long inicio = System.nanoTime();

                metricasFinais = algoritmo.ordenar(copia, comparador);

                tempos[i] = System.nanoTime() - inicio;
            }

            Arrays.sort(tempos);

            resultados.add(new Resultado(
                nomeExibicao(algoritmo),
                tempos[REPETICOES / 2],
                metricasFinais.getComparacoes(),
                metricasFinais.getTrocas()
            ));
        }


        return resultados;
    }

    private List<Post> prepararEntrada(
            List<Post> posts,
            Comparator<Post> comparador,
            TipoDados tipoDados) {

        List<Post> entrada = new ArrayList<>(posts);
        entrada.sort(comparador);

        Random random = new Random(SEED);

        switch (tipoDados) {
            case ALEATORIO -> Collections.shuffle(entrada, random);

            case QUASE_ORDENADO -> {
                int alteracoes = Math.max(1, entrada.size() / 20);

                for (int i = 0; i < alteracoes; i++) {
                    int a = random.nextInt(entrada.size());
                    int b = random.nextInt(entrada.size());

                    Collections.swap(entrada, a, b);
                }
            }

            case INVERSO -> Collections.reverse(entrada);
        }

        return entrada;
    }

    private String nomeExibicao(Algoritmo algoritmo) {
        return switch (algoritmo) {
            case BUBBLE_SORT -> "Bubble Sort";
            case INSERTION_SORT -> "Insertion Sort";
            case MERGE_SORT -> "Merge Sort";
            case QUICK_SORT -> "Quick Sort";
        };
    }
}
