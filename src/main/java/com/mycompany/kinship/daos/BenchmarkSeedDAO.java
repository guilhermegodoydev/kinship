
package com.mycompany.kinship.dao;

import com.mycompany.kinship.database.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BenchmarkSeedDAO {

    private static final String PREFIXO = "kinship_bench_";
    private static final int TAMANHO_LOTE = 500;

    private static final String[] NOMES = {
        "Ana", "Bruno", "Camila", "Daniel", "Eduarda",
        "Felipe", "Gabriela", "Henrique", "Isabela", "Lucas",
        "Marina", "Rafael", "Sofia", "Thiago", "Valentina"
    };

    private static final String[] SOBRENOMES = {
        "Silva", "Santos", "Oliveira", "Souza", "Costa",
        "Pereira", "Almeida", "Lima", "Ribeiro", "Ferreira"
    };

    private static final String[] TEXTOS_POST = {
        "Hoje foi um dia cheio de descobertas.",
        "Compartilhando um momento especial!",
        "Alguém tem uma recomendação para o fim de semana?",
        "Pequenas conquistas também merecem ser comemoradas.",
        "Testando esta rede social.",
        "Uma ideia interessante para discutir.",
        "Que todos tenham uma ótima semana!",
        "Aprender algo novo todos os dias faz diferença."
    };

    private static final String[] TEXTOS_COMENTARIO = {
        "Muito bom!",
        "Concordo com você.",
        "Obrigado por compartilhar.",
        "Gostei dessa ideia.",
        "Interessante!",
        "Ótima publicação!"
    };

    public int gerar(int quantidadePosts) throws SQLException {
        if (quantidadePosts < 1 || quantidadePosts > 1_000_000) {
            throw new IllegalArgumentException(
                    "A quantidade deve estar entre 1 e 100.000.");
        }

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            try {
                apagarDados(conexao);

                int quantidadeUsuarios =
                        Math.max(100, Math.min(quantidadePosts, 5_000));

                inserirUsuarios(conexao, quantidadeUsuarios);

                List<Integer> usuarios = buscarCodigosUsuarios(conexao);

                inserirPosts(conexao, quantidadePosts, usuarios);

                List<Integer> posts = buscarCodigosPosts(conexao);

                inserirComentarios(conexao, posts, usuarios);

                inserirCurtidasPosts(conexao, posts, usuarios);

                conexao.commit();
                return quantidadePosts;

            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        }
    }

    public void apagar() throws SQLException {
        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            try {
                apagarDados(conexao);
                conexao.commit();
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        }
    }

    private void apagarDados(Connection conexao) throws SQLException {
        String prefixoSql = "LEFT(u.nickname, ?) = ?";

        String[] comandos = {
            """
            DELETE cc
            FROM dbo.curtida_comentario cc
            JOIN dbo.comentario c ON c.codigo = cc.codigo_comentario
            JOIN dbo.post p ON p.codigo = c.codigo_post
            JOIN dbo.usuario u ON u.codigo = p.codigo_usuario
            WHERE LEFT(u.nickname, ?) = ?
            """,
            """
            DELETE cp
            FROM dbo.curtida_post cp
            JOIN dbo.post p ON p.codigo = cp.codigo_post
            JOIN dbo.usuario u ON u.codigo = p.codigo_usuario
            WHERE LEFT(u.nickname, ?) = ?
            """,
            """
            DELETE c
            FROM dbo.comentario c
            JOIN dbo.post p ON p.codigo = c.codigo_post
            JOIN dbo.usuario u ON u.codigo = p.codigo_usuario
            WHERE LEFT(u.nickname, ?) = ?
            """,
            """
            DELETE p
            FROM dbo.post p
            JOIN dbo.usuario u ON u.codigo = p.codigo_usuario
            WHERE LEFT(u.nickname, ?) = ?
            """,
            """
            DELETE FROM dbo.usuario
            WHERE LEFT(nickname, ?) = ?
            """
        };

        for (String sql : comandos) {
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                comando.setInt(1, PREFIXO.length());
                comando.setString(2, PREFIXO);
                comando.executeUpdate();
            }
        }
    }

    private void inserirUsuarios(Connection conexao, int quantidade)
            throws SQLException {
        String sql = """
            INSERT INTO dbo.usuario
                (nome, nickname, email, senha, biografia, foto_perfil)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        Random random = new Random(20261025L);

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (int i = 1; i <= quantidade; i++) {
                String nickname = PREFIXO + i;

                comando.setString(1, escolher(NOMES, random) + " "
                        + escolher(SOBRENOMES, random));
                comando.setString(2, nickname);
                comando.setString(3, nickname + "@kinship.test");
                comando.setString(4,
                        "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
                comando.setString(5, "Perfil fictício para testes.");
                comando.setString(6, null);
                comando.addBatch();

                if (i % TAMANHO_LOTE == 0) {
                    comando.executeBatch();
                }
            }

            comando.executeBatch();
        }
    }

    private List<Integer> buscarCodigosUsuarios(Connection conexao)
            throws SQLException {
        List<Integer> codigos = new ArrayList<>();

        String sql = """
            SELECT codigo
            FROM dbo.usuario
            WHERE LEFT(nickname, ?) = ?
            ORDER BY codigo
            """;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, PREFIXO.length());
            comando.setString(2, PREFIXO);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    codigos.add(resultado.getInt("codigo"));
                }
            }
        }

        return codigos;
    }

    private void inserirPosts(
            Connection conexao,
            int quantidade,
            List<Integer> usuarios) throws SQLException {
        String sql = """
            INSERT INTO dbo.post (conteudo, data_publicacao, codigo_usuario)
            VALUES (?, ?, ?)
            """;

        Random random = new Random(20261026L);
        LocalDateTime inicio = LocalDateTime.of(2023, 1, 1, 8, 0);

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (int i = 1; i <= quantidade; i++) {
                comando.setString(1, TEXTOS_POST[random.nextInt(TEXTOS_POST.length)]
                        + " Post de teste " + i + ".");
                comando.setTimestamp(2, Timestamp.valueOf(
                        inicio.plusMinutes(random.nextInt(1_600_000))));
                comando.setInt(3, escolherCodigo(usuarios, random));
                comando.addBatch();

                if (i % TAMANHO_LOTE == 0) {
                    comando.executeBatch();
                }
            }

            comando.executeBatch();
        }
    }

    private List<Integer> buscarCodigosPosts(Connection conexao)
            throws SQLException {
        List<Integer> codigos = new ArrayList<>();

        String sql = """
            SELECT p.codigo
            FROM dbo.post p
            JOIN dbo.usuario u ON u.codigo = p.codigo_usuario
            WHERE LEFT(u.nickname, ?) = ?
            ORDER BY p.codigo
            """;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, PREFIXO.length());
            comando.setString(2, PREFIXO);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    codigos.add(resultado.getInt("codigo"));
                }
            }
        }

        return codigos;
    }

    private void inserirComentarios(
            Connection conexao,
            List<Integer> posts,
            List<Integer> usuarios) throws SQLException {
        String sql = """
            INSERT INTO dbo.comentario
                (texto, data_hora, codigo_post, codigo_usuario)
            VALUES (?, ?, ?, ?)
            """;

        Random random = new Random(20261027L);
        LocalDateTime inicio = LocalDateTime.of(2023, 1, 1, 8, 0);
        int total = 0;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (int codigoPost : posts) {
                int quantidade = random.nextInt(4);

                for (int i = 0; i < quantidade; i++) {
                    comando.setString(1,
                            TEXTOS_COMENTARIO[random.nextInt(TEXTOS_COMENTARIO.length)]);
                    comando.setTimestamp(2, Timestamp.valueOf(
                            inicio.plusMinutes(random.nextInt(1_600_000))));
                    comando.setInt(3, codigoPost);
                    comando.setInt(4, escolherCodigo(usuarios, random));
                    comando.addBatch();
                    total++;

                    if (total % TAMANHO_LOTE == 0) {
                        comando.executeBatch();
                    }
                }
            }

            comando.executeBatch();
        }
    }

    private void inserirCurtidasPosts(
            Connection conexao,
            List<Integer> posts,
            List<Integer> usuarios) throws SQLException {
        String sql = """
            INSERT INTO dbo.curtida_post (codigo_post, codigo_usuario)
            VALUES (?, ?)
            """;

        Random random = new Random(20261028L);
        int total = 0;

        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (int codigoPost : posts) {
                int quantidade = random.nextInt(Math.min(10, usuarios.size()) + 1);
                java.util.Set<Integer> escolhidos = new java.util.HashSet<>();

                while (escolhidos.size() < quantidade) {
                    escolhidos.add(escolherCodigo(usuarios, random));
                }

                for (int codigoUsuario : escolhidos) {
                    comando.setInt(1, codigoPost);
                    comando.setInt(2, codigoUsuario);
                    comando.addBatch();
                    total++;

                    if (total % TAMANHO_LOTE == 0) {
                        comando.executeBatch();
                    }
                }
            }

            comando.executeBatch();
        }
    }

    private int escolherCodigo(List<Integer> codigos, Random random) {
        return codigos.get(random.nextInt(codigos.size()));
    }

    private String escolher(String[] valores, Random random) {
        return valores[random.nextInt(valores.length)];
    }
}
