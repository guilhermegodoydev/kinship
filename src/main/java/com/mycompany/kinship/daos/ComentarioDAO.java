package com.mycompany.kinship.dao;

import com.mycompany.kinship.database.Conexao;
import com.mycompany.kinship.models.Comentario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDAO {

    private static final String CONSULTA_COMENTARIOS = """
            SELECT
                c.codigo,
                c.texto,
                c.data_hora,
                c.codigo_post,
                c.codigo_usuario,
                u.nome AS autor_nome,
                u.foto_perfil AS autor_foto,
                (SELECT COUNT(*)
                 FROM dbo.curtida_comentario cc
                 WHERE cc.codigo_comentario = c.codigo) AS total_curtidas
            FROM dbo.comentario c
            INNER JOIN dbo.usuario u ON u.codigo = c.codigo_usuario
            """;

    public void cadastrar(Comentario comentario) throws SQLException {
        String sql = """
                INSERT INTO dbo.comentario
                    (texto, data_hora, codigo_post, codigo_usuario)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, comentario.getTexto());
            comando.setTimestamp(2, Timestamp.valueOf(comentario.getDataHora()));
            comando.setInt(3, comentario.getCodigoPost());
            comando.setInt(4, comentario.getCodigoUsuario());

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    comentario.setCodigo(resultado.getInt(1));
                }
            }
        }
    }

    public Comentario buscarPorCodigo(int codigo) throws SQLException {
        String sql = CONSULTA_COMENTARIOS + " WHERE c.codigo = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigo);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return mapearComentario(resultado);
                }
            }
        }

        return null;
    }

    public List<Comentario> listarPorPost(int codigoPost) throws SQLException {
        String sql = CONSULTA_COMENTARIOS
                + " WHERE c.codigo_post = ?"
                + " ORDER BY c.data_hora ASC, c.codigo ASC";

        List<Comentario> comentarios = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigoPost);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    comentarios.add(mapearComentario(resultado));
                }
            }
        }

        return comentarios;
    }

    public List<Comentario> listarPorUsuario(int codigoUsuario) throws SQLException {
        String sql = CONSULTA_COMENTARIOS
                + " WHERE c.codigo_usuario = ?"
                + " ORDER BY c.data_hora DESC, c.codigo DESC";

        List<Comentario> comentarios = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigoUsuario);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    comentarios.add(mapearComentario(resultado));
                }
            }
        }

        return comentarios;
    }

    public boolean atualizarTexto(Comentario comentario) throws SQLException {
        String sql = """
                UPDATE dbo.comentario
                SET texto = ?
                WHERE codigo = ? AND codigo_usuario = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, comentario.getTexto());
            comando.setInt(2, comentario.getCodigo());
            comando.setInt(3, comentario.getCodigoUsuario());

            return comando.executeUpdate() > 0;
        }
    }

    public boolean excluir(int codigoComentario, int codigoUsuario) throws SQLException {
        String sql = """
                DELETE FROM dbo.comentario
                WHERE codigo = ? AND codigo_usuario = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigoComentario);
            comando.setInt(2, codigoUsuario);

            return comando.executeUpdate() > 0;
        }
    }

    private Comentario mapearComentario(ResultSet resultado) throws SQLException {
        Comentario comentario = new Comentario(
                resultado.getInt("codigo"),
                resultado.getString("texto"),
                resultado.getTimestamp("data_hora").toLocalDateTime(),
                resultado.getInt("codigo_post"),
                resultado.getInt("codigo_usuario")
        );

        comentario.definirAutor(
                resultado.getString("autor_nome"),
                resultado.getString("autor_foto")
        );
        comentario.setTotalCurtidas(resultado.getInt("total_curtidas"));

        return comentario;
    }
}
