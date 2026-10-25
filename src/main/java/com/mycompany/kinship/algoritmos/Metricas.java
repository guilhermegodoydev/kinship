package com.mycompany.kinship.algoritmos;

public class Metricas {

    private long comparacoes;
    private long trocas;

    public void registrarComparacao() { comparacoes++; }
    public void registrarTroca() { trocas++; }

    public long getComparacoes() { return comparacoes; }
    public long getTrocas() { return trocas; }
}