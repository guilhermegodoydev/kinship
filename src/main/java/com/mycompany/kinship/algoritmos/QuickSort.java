package com.mycompany.kinship.algoritmos;

import java.util.Comparator;

public class QuickSort {

    public static <T> Metricas ordenar(T[] array, Comparator<T> comparador) {
        Metricas metricas = new Metricas();
        if (array == null || array.length < 2) {
            return metricas;
        }
        executar(array, 0, array.length - 1, comparador, metricas);
        return metricas;
    }

    private static <T> void executar(T[] a, int baixo, int alto, Comparator<T> c, Metricas m) {
        while (baixo < alto) {
            int p = particionar(a, baixo, alto, c, m);
            if (p - baixo < alto - p) {
                executar(a, baixo, p - 1, c, m);
                baixo = p + 1;
            } else {
                executar(a, p + 1, alto, c, m);
                alto = p - 1;
            }
        }
    }

    private static <T> int particionar(T[] a, int baixo, int alto, Comparator<T> c, Metricas m) {
        if (alto - baixo >= 2) {
            int meio = baixo + (alto - baixo) / 2;
            if (comparar(a[meio], a[baixo], c, m) < 0) trocar(a, meio, baixo, m);
            if (comparar(a[alto], a[baixo], c, m) < 0) trocar(a, alto, baixo, m);
            if (comparar(a[alto], a[meio], c, m) < 0) trocar(a, alto, meio, m);
            trocar(a, meio, alto, m);
        }

        T pivo = a[alto];
        int i = baixo - 1;
        for (int j = baixo; j < alto; j++) {
            if (comparar(a[j], pivo, c, m) <= 0) {
                i++;
                trocar(a, i, j, m);
            }
        }
        trocar(a, i + 1, alto, m);
        return i + 1;
    }

    private static <T> int comparar(T x, T y, Comparator<T> c, Metricas m) {
        m.registrarComparacao();
        return c.compare(x, y);
    }

    private static <T> void trocar(T[] a, int i, int j, Metricas m) {
        if (i == j) return;
        T temporario = a[i];
        a[i] = a[j];
        a[j] = temporario;
        m.registrarTroca();
    }
}