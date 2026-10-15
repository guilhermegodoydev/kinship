package com.mycompany.kinship.models;

import java.time.LocalDateTime;

public class Post {

    private Integer codigo;
    private String conteudo;
    private LocalDateTime dataPublicacao;
    private int codigoUsuario;

    private int totalCurtidas;
    private int totalComentarios;
    private String autorNome;
    private String autorFoto;

    public Post(String conteudo, int codigoUsuario) {
        this(null, conteudo, LocalDateTime.now(), codigoUsuario);
    }

    public Post(Integer codigo, String conteudo, LocalDateTime dataPublicacao, int codigoUsuario) {
        this.codigo = codigo;
        this.conteudo = Validacao.obrigatorio(conteudo, "Conteúdo", Integer.MAX_VALUE);
        if (dataPublicacao == null) {
            throw new IllegalArgumentException("Data de publicação é obrigatória.");
        }
        this.dataPublicacao = dataPublicacao;
        this.codigoUsuario = Validacao.idPositivo(codigoUsuario, "Autor");
    }

    public void editarConteudo(String novoConteudo) {
        this.conteudo = Validacao.obrigatorio(novoConteudo, "Conteúdo", Integer.MAX_VALUE);
    }

    public void definirAutor(String nome, String foto) {
        this.autorNome = nome;
        this.autorFoto = foto;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public int getCodigoUsuario() {
        return codigoUsuario;
    }

    public int getTotalCurtidas() {
        return totalCurtidas;
    }

    public int getTotalComentarios() {
        return totalComentarios;
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

    public void setTotalComentarios(int totalComentarios) {
        this.totalComentarios = Validacao.naoNegativo(totalComentarios, "Total de comentários");
    }
}