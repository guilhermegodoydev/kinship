package com.mycompany.kinship.algoritmos;

import java.util.Comparator;

public class InsertionSort {
    public static <T> void ordenar(T[] array, Comparator<T> comparador) {
        if (array == null || array.length < 2) {
            return;
        }

        for (int i = 1; i < array.length; i++) {

            T elementoAtual = array[i];
            int j = i - 1;

            while (j >= 0 && comparador.compare(array[j], elementoAtual) > 0) {

                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = elementoAtual;
        }
    }

}
