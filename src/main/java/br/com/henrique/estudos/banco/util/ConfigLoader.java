package br.com.henrique.estudos.banco.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties prop = new Properties();

    static {
        try (InputStream fis = Conexao.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(fis);
        } catch (IOException e) {
            System.err.println("Erro ao carregar config.properties: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }
}