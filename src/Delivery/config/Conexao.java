package Delivery.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/delivery_system";
    private static final String USER = "root";
    private static final String PASS = "paik2548"; //201005Jv

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        }
        catch (SQLException e) {
            throw new SQLException("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}
