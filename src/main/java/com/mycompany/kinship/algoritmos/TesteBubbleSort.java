/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kinship.algoritmos;

/**
 *
 * @author marcia juliana
 */
import java.util.Arrays;
import java.util.Comparator;

public class TesteBubbleSort {

    public static void main(String[] args) {

        Integer[] numeros = {5, 4, 3, 2, 1};

        System.out.println("Antes da ordenação: "
                + Arrays.toString(numeros));

        BubbleSort.ordenar(numeros, Comparator.naturalOrder());

        System.out.println("Depois da ordenação: "
                + Arrays.toString(numeros));
    }
}