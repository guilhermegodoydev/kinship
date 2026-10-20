/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kinship.algoritmos;

import java.util.Arrays;
import java.util.Comparator;

public class TesteMergesort {

    public static void main(String[] args) {

        // Array para testar
        Integer[] numeros = {8, 7, 6, 5, 4, 3, 2, 1};

        System.out.println("Antes da ordenação:");
        System.out.println(Arrays.toString(numeros));

        // Executa o Merge Sort
        Mergesort.ordenar(numeros, Comparator.naturalOrder());

        System.out.println("Depois da ordenação:");
        System.out.println(Arrays.toString(numeros));
    }
}