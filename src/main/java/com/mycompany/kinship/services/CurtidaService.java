package com.mycompany.kinship.services;

import com.mycompany.kinship.daos.CurtidaPostDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class CurtidaService {

    public record Resultado(boolean curtido, int total) {}

    private final CurtidaPostDAO dao = new CurtidaPostDAO();

    public Resultado alternar(int codigoPost, int codigoUsuario) {
        try {
            boolean curtido = dao.alternar(codigoPost, codigoUsuario);
            return new Resultado(curtido, dao.contar(codigoPost));
        } catch (SQLException e) {
            throw new ErroDeAcessoException("Falha ao curtir o post.", e);
        }
    }

    public Set<Integer> postsCurtidos(int codigoUsuario, List<Integer> codigosPost) {
        try {
            return dao.buscarCurtidos(codigoUsuario, codigosPost);
        } catch (SQLException e) {
            throw new ErroDeAcessoException("Falha ao consultar as curtidas.", e);
        }
    }
}