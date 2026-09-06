/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kinship.algoritmos;

import java.util.Comparator;

public class Mergesort {
    public static <T> void ordenar(T[] array, Comparator<T> comparador) {
        if (array == null || array.length < 2) return;
        executarMergeSort(array, 0, array.length - 1, comparador);
    }

    private static <T> void executarMergeSort(T[] array, int inicio, int fim, Comparator<T> comparador) {
        if (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;
            
            executarMergeSort(array, inicio, meio, comparador);
            executarMergeSort(array, meio + 1, fim, comparador);
            
            merge(array, inicio, meio, fim, comparador);
        }
    }

    private static <T> void merge(T[] array, int inicio, int meio, int fim, Comparator<T> comparador) {
        int tamanho = fim - inicio + 1;
        T[] novoArray = (T[]) new Object[tamanho];

        int i = inicio;
        int j = meio + 1;
        int k = 0;

        while (i <= meio && j <= fim) {
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
        }
    }
}
