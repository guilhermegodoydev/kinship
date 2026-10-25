package com.mycompany.kinship.services;

import com.mycompany.kinship.daos.ComentarioDAO;
import com.mycompany.kinship.daos.CurtidaComentarioDAO;
import com.mycompany.kinship.models.Comentario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComentarioService {

    public static final int TAMANHO_PAGINA = 10;

    public record Pagina(List<Comentario> itens, Set<Integer> curtidos, boolean temMais) {}

    private final ComentarioDAO dao = new ComentarioDAO();
    private final CurtidaComentarioDAO curtidaDAO = new CurtidaComentarioDAO();

    public Pagina listarPagina(int codigoPost, int codigoUsuario, int deslocamento) {
        try {
            List<Comentario> lidos = dao.listarPagina(codigoPost, deslocamento, TAMANHO_PAGINA + 1);
            boolean temMais = lidos.size() > TAMANHO_PAGINA;
            List<Comentario> itens = new ArrayList<>(
                    temMais ? lidos.subList(0, TAMANHO_PAGINA) : lidos);

            List<Integer> codigos = itens.stream().map(Comentario::getCodigo).toList();
            Set<Integer> curtidos = curtidaDAO.buscarCurtidos(codigoUsuario, codigos);

            return new Pagina(itens, curtidos, temMais);
        } catch (SQLException e) {
            throw new ErroDeAcessoException("Falha ao carregar os comentários.", e);
        }
    }

    public Comentario comentar(int codigoPost, int codigoUsuario, String texto)
            throws ValidacaoException {
        Comentario comentario;
        try {
            comentario = new Comentario(texto, codigoPost, codigoUsuario);
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException(Map.of("texto", e.getMessage()));
        }

        try {
            dao.cadastrar(comentario);
        } catch (SQLException e) {
            throw new ErroDeAcessoException("Falha ao salvar o comentário.", e);
        }
        return comentario;
    }
}