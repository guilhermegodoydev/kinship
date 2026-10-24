package com.mycompany.kinship.models;

import java.time.LocalDateTime;

public class Usuario {

    private Integer codigo;
    private String nome;
    private String nickname;
    private String email;
    private String senha;
    private String biografia;
    private String fotoPerfil;
    private LocalDateTime dataCadastro;

    public Usuario(String nome, String nickname, String email, String senhaHash) {
        this(null, nome, nickname, email, senhaHash, null, null, null);
    }

    public Usuario(Integer codigo, String nome, String nickname, String email, String senhaHash,
                   String biografia, String fotoPerfil) {
        this(codigo, nome, nickname, email, senhaHash, biografia, fotoPerfil, null);
    }

    public Usuario(Integer codigo, String nome, String nickname, String email, String senhaHash,
                   String biografia, String fotoPerfil, LocalDateTime dataCadastro) {
        this.codigo = codigo;
        this.nome = Validacao.obrigatorio("nome", nome, "Nome", 100);
        this.nickname = Validacao.nickname("nickname", nickname);
        this.email = Validacao.email("email", email, 150);
        this.senha = Validacao.obrigatorio("senha", senhaHash, "Senha", 255);
        this.biografia = Validacao.opcional("biografia", biografia, "Biografia", 300);
        this.fotoPerfil = Validacao.opcional("fotoPerfil", fotoPerfil, "Foto de perfil", 255);
        this.dataCadastro = dataCadastro;
    }

    public void atualizarPerfil(String nome, String biografia, String fotoPerfil) {
        this.nome = Validacao.obrigatorio("nome", nome, "Nome", 100);
        this.biografia = Validacao.opcional("biografia", biografia, "Biografia", 300);
        this.fotoPerfil = Validacao.opcional("fotoPerfil", fotoPerfil, "Foto de perfil", 255);
    }

    public void alterarSenha(String novoHash) {
        this.senha = Validacao.obrigatorio("senha", novoHash, "Senha", 255);
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getBiografia() {
        return biografia;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setCodigo(int codigo) {
        this.codigo = Validacao.idPositivo(codigo, "Código");
    }
}
