/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kinship.algoritmos;

import java.util.Comparator;

public class BubbleSort {

    public static <T> void ordenar(T[] array, Comparator<T> comparador) {
        if (array == null || array.length < 2) {
            return;
        }

        // Bubble Sort com otimização
        for (int i = 0; i < array.length - 1; i++) {

            boolean trocou = false;

            for (int j = 0; j < array.length - 1 - i; j++) {

                if (comparador.compare(array[j], array[j + 1]) > 0) {
                    T temporario = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temporario;

                    trocou = true;
                }
            }

            // Se não houve trocas, o array já está ordenado
            if (!trocou) {
                break;
            }
        }
    }
}