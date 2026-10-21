package com.mycompany.kinship.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=Kinship;";
    private static final String USUARIO = "usuario";
    private static final String SENHA = "senha";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}