package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String url = "jdbc:mysql://localhost:3306/delivery_system";
    private static final String user = "root";
    private static final String password = "201005Jv?";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url,user,password);
        }catch (SQLException e){
            throw new RuntimeException("Erro ao conectar no banco" + e);
        }
    }

}
