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

        Map<String, String> erros =
                new LinkedHashMap<>(Usuario.validarDadosCadastro(nome, nickname, email));

        if (senha.isEmpty()) {
            erros.put("senha", "Informe a senha.");
        } else if (senha.length() < 8) {
            erros.put("senha", "A senha deve ter no mínimo 8 caracteres.");
        } else if (senha.length() > 72) {
            erros.put("senha", "A senha deve ter no máximo 72 caracteres.");
        }

        if (confirmacao.isEmpty()) {
            erros.put("confirmarSenha", "Confirme a senha.");
        } else if (!senha.equals(confirmacao)) {
            erros.put("confirmarSenha", "As senhas não coincidem.");
        }

        try {
            if (!erros.containsKey("nickname")
                    && dao.buscarPorNickname(Usuario.normalizarNickname(nickname)) != null) {
                erros.put("nickname", "Este nickname já está em uso.");
            }
            if (!erros.containsKey("email")
                    && dao.buscarPorEmail(Usuario.normalizarEmail(email)) != null) {
                erros.put("email", "Este e-mail já está cadastrado.");
            }
        } catch (SQLException e) {
            throw new ErroDeAcessoException("Falha ao acessar o banco de dados.", e);
        }

        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }

        String hash = BCrypt.withDefaults().hashToString(12, senha.toCharArray());
        Usuario usuario = new Usuario(nome, nickname, email, hash);

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

    public Optional<Usuario> autenticar(String email, String senha) throws ValidacaoException {
        Map<String, String> erros = new LinkedHashMap<>();

        String emailNormalizado = null;
        try {
            emailNormalizado = Usuario.normalizarEmail(email);
        } catch (DominioException e) {
            erros.put("email", e.getMessage());
        }

        if (senha == null || senha.isEmpty()) {
            erros.put("senha", "Informe a senha.");
        }

        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }

        try {
            Usuario usuario = dao.buscarPorEmail(emailNormalizado);
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