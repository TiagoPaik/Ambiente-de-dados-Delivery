package Delivery.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/delivery_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";     // altere se necessário
    private static final String PASS = "root";     // altere se necessário

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
