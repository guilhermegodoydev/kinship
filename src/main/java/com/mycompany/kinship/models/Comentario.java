package com.mycompany.kinship.models;

import java.time.LocalDateTime;

public class Comentario {

    private Integer codigo;
    private String texto;
    private LocalDateTime dataHora;
    private int codigoPost;
    private int codigoUsuario;

    private int totalCurtidas;
    private String autorNome;
    private String autorFoto;

    public Comentario(String texto, int codigoPost, int codigoUsuario) {
        this(null, texto, LocalDateTime.now(), codigoPost, codigoUsuario);
    }

    public Comentario(Integer codigo, String texto, LocalDateTime dataHora, int codigoPost, int codigoUsuario) {
        this.codigo = codigo;
        this.texto = Validacao.obrigatorio(texto, "Comentário", 500);
        if (dataHora == null) {
            throw new IllegalArgumentException("Data e hora são obrigatórias.");
        }
        this.dataHora = dataHora;
        this.codigoPost = Validacao.idPositivo(codigoPost, "Post");
        this.codigoUsuario = Validacao.idPositivo(codigoUsuario, "Autor");
    }

    public void editarTexto(String novoTexto) {
        this.texto = Validacao.obrigatorio(novoTexto, "Comentário", 500);
    }

    public void definirAutor(String nome, String foto) {
        this.autorNome = nome;
        this.autorFoto = foto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getTexto() {
        return texto;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public int getCodigoPost() {
        return codigoPost;
    }

    public int getCodigoUsuario() {
        return codigoUsuario;
    }

    public int getTotalCurtidas() {
        return totalCurtidas;
    }

    public String getAutorNome() {
        return autorNome;
    }

    public String getAutorFoto() {
        return autorFoto;
    }

    public void setCodigo(int codigo) {
        this.codigo = Validacao.idPositivo(codigo, "Código");
    }

    public void setTotalCurtidas(int totalCurtidas) {
        this.totalCurtidas = Validacao.naoNegativo(totalCurtidas, "Total de curtidas");
    }
}