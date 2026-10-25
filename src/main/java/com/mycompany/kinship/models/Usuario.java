package com.mycompany.kinship.models;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class Usuario {

    private Integer codigo;
    private String nome;
    private String nickname;
    private String email;
    private String senha;
    private String biografia;
    private String fotoPerfil;
    private LocalDateTime dataCadastro;
    
    private static final int NOME_MAX = 100;
    private static final int EMAIL_MAX = 150;

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
        this.nome = Validacao.obrigatorio("nome", nome, "Nome", NOME_MAX);
        this.nickname = Validacao.nickname("nickname", nickname);
        this.email = Validacao.email("email", email, EMAIL_MAX);
        this.senha = Validacao.obrigatorio("senha", senhaHash, "Senha", 255);
        this.biografia = Validacao.opcional("biografia", biografia, "Biografia", 300);
        this.fotoPerfil = Validacao.opcional("fotoPerfil", fotoPerfil, "Foto de perfil", 255);
        this.dataCadastro = dataCadastro;
    }
    
    public static Map<String, String> validarDadosCadastro(String nome, String nickname, String email) {
        Map<String, String> erros = new LinkedHashMap<>();
        coletar(erros, () -> Validacao.obrigatorio("nome", nome, "Nome", NOME_MAX));
        coletar(erros, () -> Validacao.nickname("nickname", nickname));
        coletar(erros, () -> Validacao.email("email", email, EMAIL_MAX));
        return erros;
    }
    
    public static String normalizarNickname(String nickname) {
        return Validacao.nickname("nickname", nickname);
    }
    
        private static void coletar(Map<String, String> erros, Runnable validacao) {
        try {
            validacao.run();
        } catch (DominioException e) {
            erros.putIfAbsent(e.getCampo(), e.getMessage());
        }
    }
    
    public static String normalizarEmail(String email) {
        return Validacao.email("email", email, EMAIL_MAX);
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
