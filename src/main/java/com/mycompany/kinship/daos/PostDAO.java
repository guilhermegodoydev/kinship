package com.mycompany.kinship.dao;

import com.mycompany.kinship.database.Conexao;
import com.mycompany.kinship.models.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    private static final String CONSULTA_POSTS = """
            SELECT
                p.codigo,
                p.conteudo,
                p.data_publicacao,
                p.codigo_usuario,
                u.nome AS autor_nome,
                u.foto_perfil AS autor_foto,
                (SELECT COUNT(*)
                 FROM dbo.curtida_post cp
                 WHERE cp.codigo_post = p.codigo) AS total_curtidas,
                (SELECT COUNT(*)
                 FROM dbo.comentario c
                 WHERE c.codigo_post = p.codigo) AS total_comentarios
            FROM dbo.post p
            INNER JOIN dbo.usuario u ON u.codigo = p.codigo_usuario
            """;

    public void cadastrar(Post post) throws SQLException {
        String sql = """
                INSERT INTO dbo.post (conteudo, data_publicacao, codigo_usuario)
                VALUES (?, ?, ?)
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, post.getConteudo());
            comando.setTimestamp(2, Timestamp.valueOf(post.getDataPublicacao()));
            comando.setInt(3, post.getCodigoUsuario());

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    post.setCodigo(resultado.getInt(1));
                }
            }
        }
    }

    public Post buscarPorCodigo(int codigo) throws SQLException {
        String sql = CONSULTA_POSTS + " WHERE p.codigo = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigo);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return mapearPost(resultado);
                }
            }
        }

        return null;
    }

    public List<Post> listarPorUsuario(int codigoUsuario) throws SQLException {
        String sql = CONSULTA_POSTS
                + " WHERE p.codigo_usuario = ?"
                + " ORDER BY p.data_publicacao DESC, p.codigo DESC";

        List<Post> posts = new ArrayList<>();

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigoUsuario);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    posts.add(mapearPost(resultado));
                }
            }
        }

        return posts;
    }

    public boolean atualizarConteudo(Post post) throws SQLException {
        String sql = """
                UPDATE dbo.post
                SET conteudo = ?
                WHERE codigo = ? AND codigo_usuario = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, post.getConteudo());
            comando.setInt(2, post.getCodigo());
            comando.setInt(3, post.getCodigoUsuario());

            return comando.executeUpdate() > 0;
        }
    }

    public boolean excluir(int codigoPost, int codigoUsuario) throws SQLException {
        String sql = """
                DELETE FROM dbo.post
                WHERE codigo = ? AND codigo_usuario = ?
                """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, codigoPost);
            comando.setInt(2, codigoUsuario);

            return comando.executeUpdate() > 0;
        }
    }

    private Post mapearPost(ResultSet resultado) throws SQLException {
        Post post = new Post(
                resultado.getInt("codigo"),
                resultado.getString("conteudo"),
                resultado.getTimestamp("data_publicacao").toLocalDateTime(),
                resultado.getInt("codigo_usuario")
        );

        post.definirAutor(
                resultado.getString("autor_nome"),
                resultado.getString("autor_foto")
        );
        post.setTotalCurtidas(resultado.getInt("total_curtidas"));
        post.setTotalComentarios(resultado.getInt("total_comentarios"));

        return post;
    }
}
