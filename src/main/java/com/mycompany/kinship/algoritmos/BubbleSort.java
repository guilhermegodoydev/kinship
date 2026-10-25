package com.mycompany.kinship.algoritmos;

import java.util.Comparator;

public class BubbleSort {

    public static <T> Metricas ordenar(T[] array, Comparator<T> comparador) {
        Metricas metricas = new Metricas();
        if (array == null || array.length < 2) {
            return metricas;
        }

        for (int i = 0; i < array.length - 1; i++) {
            boolean trocou = false;

            for (int j = 0; j < array.length - 1 - i; j++) {
                metricas.registrarComparacao();
                if (comparador.compare(array[j], array[j + 1]) > 0) {
                    T temporario = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temporario;
                    metricas.registrarTroca();
                    trocou = true;
                }
            }

            if (!trocou) {
                break;
            }
        }
        return metricas;
    }
}