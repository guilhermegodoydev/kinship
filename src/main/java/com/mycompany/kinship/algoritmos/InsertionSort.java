package com.mycompany.kinship.algoritmos;

import java.util.Comparator;

public class InsertionSort {

    public static <T> Metricas ordenar(T[] array, Comparator<T> comparador) {
        Metricas metricas = new Metricas();
        if (array == null || array.length < 2) {
            return metricas;
        }

        for (int i = 1; i < array.length; i++) {
            T elementoAtual = array[i];
            int j = i - 1;

            while (j >= 0) {
                metricas.registrarComparacao();
                if (comparador.compare(array[j], elementoAtual) <= 0) {
                    break;
                }
                array[j + 1] = array[j];
                metricas.registrarTroca();
                j--;
            }

            array[j + 1] = elementoAtual;
        }
        return metricas;
    }
}