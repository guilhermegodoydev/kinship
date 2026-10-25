package com.mycompany.kinship.daos;

import com.mycompany.kinship.database.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CurtidaPostDAO {
    public boolean alternar(int codigoPost, int codigoUsuario) throws SQLException {
        try (Connection conexao = Conexao.conectar()) {

            try (PreparedStatement remover = conexao.prepareStatement(
                    "DELETE FROM dbo.curtida_post WHERE codigo_post = ? AND codigo_usuario = ?")) {
                remover.setInt(1, codigoPost);
                remover.setInt(2, codigoUsuario);
                if (remover.executeUpdate() > 0) {
                    return false;
                }
            }

            try (PreparedStatement inserir = conexao.prepareStatement(
                    "INSERT INTO dbo.curtida_post (codigo_post, codigo_usuario) VALUES (?, ?)")) {
                inserir.setInt(1, codigoPost);
                inserir.setInt(2, codigoUsuario);
                inserir.executeUpdate();
                return true;
            } catch (SQLException e) {
                if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
                    return true;
                }
                throw e;
            }
        }
    }

    public int contar(int codigoPost) throws SQLException {
        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     "SELECT COUNT(*) FROM dbo.curtida_post WHERE codigo_post = ?")) {
            comando.setInt(1, codigoPost);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? resultado.getInt(1) : 0;
            }
        }
    }

    public Set<Integer> buscarCurtidos(int codigoUsuario, List<Integer> codigosPost)
            throws SQLException {
        Set<Integer> curtidos = new HashSet<>();
        if (codigosPost.isEmpty()) {
            return curtidos;
        }

        String marcadores = String.join(",", Collections.nCopies(codigosPost.size(), "?"));
        String sql = "SELECT codigo_post FROM dbo.curtida_post "
                + "WHERE codigo_usuario = ? AND codigo_post IN (" + marcadores + ")";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigoUsuario);
            for (int i = 0; i < codigosPost.size(); i++) {
                comando.setInt(i + 2, codigosPost.get(i));
            }

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    curtidos.add(resultado.getInt(1));
                }
            }
        }
        return curtidos;
    }
}