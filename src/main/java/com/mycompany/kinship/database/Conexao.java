package com.mycompany.kinship.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Conexao {

    private static final Properties CONFIG = carregarConfig();

    private static Properties carregarConfig() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            p.load(in);
        } catch (IOException e) {
            throw new RuntimeException(
                "config.properties não encontrado. Copie config.example.properties para config.properties e preencha.", e);
        }
        return p;
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(
            CONFIG.getProperty("db.url"),
            CONFIG.getProperty("db.usuario"),
            CONFIG.getProperty("db.senha"));
    }
    public static void main(String[] args) throws Exception { 
        try (Connection c = Conexao.conectar();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM usuario")) {
            rs.next();
            System.out.println("Conectou! Usuarios: " + rs.getInt(1));
        }
        
        System.out.println("url     = [" + CONFIG.getProperty("db.url") + "]");
        System.out.println("usuario = [" + CONFIG.getProperty("db.usuario") + "]");
        String s = CONFIG.getProperty("db.senha");
        System.out.println("senha tem " + (s == null ? "null" : s.length() + " caracteres"));
        System.out.println("chaves lidas: " + CONFIG.stringPropertyNames());
    }
}
