package Delivery.dao;

import Delivery.config.Conexao;
import Delivery.modelo.Admin;

import java.sql.*;

public class AdminDAO {

    public Admin inserir(Admin a) throws SQLException {
        String sql = "INSERT INTO Admin(email, senha) VALUES(?,?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getEmail());
            ps.setString(2, a.getSenha());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setIdAdmin(rs.getInt(1));
            }
            return a;
        }
    }

    public Admin login(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM Admin WHERE email=? AND senha=?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(rs.getInt("id_admin"), rs.getString("email"), rs.getString("senha"));
                }
            }
        }
        return null;
    }
}
