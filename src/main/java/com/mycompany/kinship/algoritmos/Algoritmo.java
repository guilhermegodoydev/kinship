package com.mycompany.kinship.algoritmos;

import java.util.Comparator;

public enum Algoritmo {
    BUBBLE_SORT, INSERTION_SORT, MERGE_SORT, QUICK_SORT;

    public <T> Metricas ordenar(T[] array, Comparator<T> comparador) {
        return switch (this) {
            case BUBBLE_SORT -> BubbleSort.ordenar(array, comparador);
            case INSERTION_SORT -> InsertionSort.ordenar(array, comparador);
            case MERGE_SORT -> Mergesort.ordenar(array, comparador);
            case QUICK_SORT -> QuickSort.ordenar(array, comparador);
        };
    }
}