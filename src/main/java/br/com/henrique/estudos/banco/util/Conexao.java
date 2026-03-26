package br.com.henrique.estudos.banco.util;

import br.com.henrique.estudos.banco.exceptions.ConfiguracaoBancoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection getConnection() throws SQLException {
        try {
            String url = ConfigLoader.get("db.url");
            String user = ConfigLoader.get("db.user");
            String pass = ConfigLoader.get("db.pass");

            if (url == null || user == null || pass == null) {
                throw new ConfiguracaoBancoException("ERRO: Preencha as credencias do arquivo config.properties!");
            }

            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            throw new SQLException("Erro inesperado ao tentar conectar: " + e.getMessage());
        }
    }
}