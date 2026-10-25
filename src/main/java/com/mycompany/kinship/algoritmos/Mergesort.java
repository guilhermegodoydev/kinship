package com.mycompany.kinship.algoritmos;

import java.util.Comparator;

public class Mergesort {

    public static <T> Metricas ordenar(T[] array, Comparator<T> comparador) {
        Metricas metricas = new Metricas();
        if (array == null || array.length < 2) {
            return metricas;
        }
        executarMergeSort(array, 0, array.length - 1, comparador, metricas);
        return metricas;
    }

    private static <T> void executarMergeSort(T[] array, int inicio, int fim,
                                              Comparator<T> comparador, Metricas metricas) {
        if (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;
            executarMergeSort(array, inicio, meio, comparador, metricas);
            executarMergeSort(array, meio + 1, fim, comparador, metricas);
            merge(array, inicio, meio, fim, comparador, metricas);
        }
    }

    private static <T> void merge(T[] array, int inicio, int meio, int fim,
                                  Comparator<T> comparador, Metricas metricas) {
        int tamanho = fim - inicio + 1;
        @SuppressWarnings("unchecked")
        T[] novoArray = (T[]) new Object[tamanho];

        int i = inicio;
        int j = meio + 1;
        int k = 0;

        while (i <= meio && j <= fim) {
            metricas.registrarComparacao();
            if (comparador.compare(array[i], array[j]) <= 0) {
                novoArray[k] = array[i];
                i++;
            } else {
                novoArray[k] = array[j];
                j++;
            }
            k++;
        }

        while (i <= meio) {
            novoArray[k] = array[i];
            i++;
            k++;
        }

        while (j <= fim) {
            novoArray[k] = array[j];
            j++;
            k++;
        }

        for (k = 0; k < tamanho; k++) {
            array[inicio + k] = novoArray[k];
            metricas.registrarTroca();
        }
    }
}