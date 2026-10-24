package com.mycompany.kinship.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.mycompany.kinship.daos.UsuarioDAO;
import com.mycompany.kinship.models.DominioException;
import com.mycompany.kinship.models.Usuario;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AutenticacaoService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario cadastrar(String nome, String nickname, String email,
                             String senha, String confirmacao) throws ValidacaoException {

        senha = senha == null ? "" : senha;
        confirmacao = confirmacao == null ? "" : confirmacao;

        Map<String, String> erros = new LinkedHashMap<>();

        
        if (senha.length() < 8) {
            erros.put("senha", "A senha deve ter no mínimo 8 caracteres.");
        } else if (senha.length() > 72) {
            erros.put("senha", "A senha deve ter no máximo 72 caracteres.");
        }
        if (!senha.equals(confirmacao)) {
            erros.put("confirmarSenha", "As senhas não coincidem.");
        }

        
        String hash = erros.isEmpty()
                ? BCrypt.withDefaults().hashToString(12, senha.toCharArray())
                : "-";

        
        Usuario usuario = null;
        try {
            usuario = new Usuario(nome, nickname, email, hash);
        } catch (DominioException e) {
            erros.put(e.getCampo(), e.getMessage());
        }

       
        if (usuario != null) {
            try {
                if (dao.buscarPorNickname(usuario.getNickname()) != null) {
                    erros.put("nickname", "Este nickname já está em uso.");
                }
                if (dao.buscarPorEmail(usuario.getEmail()) != null) {
                    erros.put("email", "Este e-mail já está cadastrado.");
                }
            } catch (SQLException e) {
                throw new ErroDeAcessoException("Falha ao acessar o banco de dados.", e);
            }
        }

        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }

        try {
            dao.cadastrar(usuario);
        } catch (SQLException e) {
            if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                erros.put("geral", "Nickname ou e-mail já cadastrado.");
                throw new ValidacaoException(erros);
            }
            throw new ErroDeAcessoException("Falha ao cadastrar usuário.", e);
        }

        return usuario;
    }

    public Optional<Usuario> autenticar(String identificador, String senha) {
        if (identificador == null || identificador.isBlank() || senha == null || senha.isEmpty()) {
            return Optional.empty();
        }

        try {
            String id = identificador.trim();
            Usuario usuario = id.contains("@")
                    ? dao.buscarPorEmail(id.toLowerCase())
                    : dao.buscarPorNickname(id);

            if (usuario == null) {
                return Optional.empty();
            }

            boolean confere = BCrypt.verifyer()
                    .verify(senha.toCharArray(), usuario.getSenha()).verified;

            return confere ? Optional.of(usuario) : Optional.empty();

        } catch (SQLException e) {
            throw new ErroDeAcessoException("Falha ao acessar o banco de dados.", e);
        }
    }
}