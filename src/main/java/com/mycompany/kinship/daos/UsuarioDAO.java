package com.mycompany.kinship.daos;

import com.mycompany.kinship.database.Conexao;
import com.mycompany.kinship.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class UsuarioDAO {

    public void cadastrar(Usuario usuario) throws SQLException {
        String sql = """
                INSERT INTO dbo.usuario
                    (nome, nickname, email, senha, biografia, foto_perfil)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getNickname());
            comando.setString(3, usuario.getEmail());
            comando.setString(4, usuario.getSenha());
            comando.setString(5, usuario.getBiografia());
            comando.setString(6, usuario.getFotoPerfil());

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    usuario.setCodigo(resultado.getInt(1));
                }
            }
        }
    }

    public Usuario buscarPorCodigo(int codigo) throws SQLException {
        String sql = """
                SELECT codigo, nome, nickname, email, senha, biografia, foto_perfil, data_cadastro
                FROM dbo.usuario
                WHERE codigo = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigo);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }
        }

        return null;
    }

    public Usuario buscarPorNickname(String nickname) throws SQLException {
        String sql = """
                SELECT codigo, nome, nickname, email, senha, biografia, foto_perfil, data_cadastro
                FROM dbo.usuario
                WHERE nickname = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, nickname);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }
        }

        return null;
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = """
                SELECT codigo, nome, nickname, email, senha, biografia, foto_perfil, data_cadastro
                FROM dbo.usuario
                WHERE email = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, email);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }
        }

        return null;
    }

    public boolean atualizarPerfil(Usuario usuario) throws SQLException {
        String sql = """
                UPDATE dbo.usuario
                SET nome = ?, biografia = ?, foto_perfil = ?
                WHERE codigo = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getBiografia());
            comando.setString(3, usuario.getFotoPerfil());
            comando.setInt(4, usuario.getCodigo());

            return comando.executeUpdate() > 0;
        }
    }

    public boolean alterarSenha(Usuario usuario) throws SQLException {
        String sql = """
                UPDATE dbo.usuario
                SET senha = ?
                WHERE codigo = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, usuario.getSenha());
            comando.setInt(2, usuario.getCodigo());

            return comando.executeUpdate() > 0;
        }
    }

    public boolean excluir(int codigo) throws SQLException {
        String sql = "DELETE FROM dbo.usuario WHERE codigo = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigo);

            return comando.executeUpdate() > 0;
        }
    }

    private Usuario mapearUsuario(ResultSet resultado) throws SQLException {
        Timestamp dataCadastro = resultado.getTimestamp("data_cadastro");

        return new Usuario(
                resultado.getInt("codigo"),
                resultado.getString("nome"),
                resultado.getString("nickname"),
                resultado.getString("email"),
                resultado.getString("senha"),
                resultado.getString("biografia"),
                resultado.getString("foto_perfil"),
                dataCadastro == null ? null : dataCadastro.toLocalDateTime()
        );
    }
}
