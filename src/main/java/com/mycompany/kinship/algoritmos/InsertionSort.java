package com.mycompany.kinship.algoritmos;


import java.util.Comparator;

public class InsertionSort {

    public static <T> void ordenar(T[] array, Comparator<T> comparador) {
        if (array == null || array.length < 2) {
            return;
        }

        boolean melhorCaso = true;
        boolean piorCaso = true;

        // Identifica o melhor caso e o pior caso
        for (int i = 0; i < array.length - 1; i++) {

            if (comparador.compare(array[i], array[i + 1]) > 0) {
                melhorCaso = false;
            }

            if (comparador.compare(array[i], array[i + 1]) < 0) {
                piorCaso = false;
            }
        }

        if (melhorCaso) {
            System.out.println("Melhor caso: O(n)");
        } else if (piorCaso) {
            System.out.println("Pior caso: O(n²)");
        } else {
            System.out.println("Caso médio: O(n²)");
        }

        // Algoritmo Insertion Sort
        for (int i = 1; i < array.length; i++) {

            T elementoAtual = array[i];
            int j = i - 1;

            while (j >= 0 &&
                   comparador.compare(array[j], elementoAtual) > 0) {

                array[j + 1] = array[j];
                j = j - 1;
            }

            array[j + 1] = elementoAtual;
        }
    }
}

