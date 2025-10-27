package Delivery.dao;

import Delivery.modelo.Restaurante;
import util.ConnectionFactory;

import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestauranteDAO {

    public List<Restaurante> listar() throws SQLException {
        String sql = "SELECT id_restaurante, nome, tipo_cozinha, telefone, endereco " +
                "FROM Restaurante ORDER BY nome";
        List<Restaurante> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    public Restaurante buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_restaurante, nome, tipo_cozinha, telefone, endereco " +
                "FROM Restaurante WHERE id_restaurante = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Restaurante inserir(Restaurante r) throws SQLException {
        // 🔹 Validação com popup
        if (r.getNome() == null || r.getNome().isBlank() ||
                r.getTipoCozinha() == null || r.getTipoCozinha().isBlank() ||
                r.getTelefone() == null || r.getTelefone().isBlank() ||
                r.getEndereco() == null || r.getEndereco().isBlank()) {

            JOptionPane.showMessageDialog(null,
                    "Todos os campos devem ser preenchidos antes de inserir o restaurante.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String sql = "INSERT INTO Restaurante (nome, tipo_cozinha, telefone, endereco) VALUES (?,?,?,?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNome());
            ps.setString(2, r.getTipoCozinha());
            ps.setString(3, r.getTelefone());
            ps.setString(4, r.getEndereco());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setIdRestaurante(rs.getInt(1));
            }
        }

        JOptionPane.showMessageDialog(null,
                "Restaurante cadastrado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);

        return r;
    }

    public void atualizar(Restaurante r) throws SQLException {
        // 🔹 Validação com popup
        if (r.getNome() == null || r.getNome().isBlank() ||
                r.getTipoCozinha() == null || r.getTipoCozinha().isBlank() ||
                r.getTelefone() == null || r.getTelefone().isBlank() ||
                r.getEndereco() == null || r.getEndereco().isBlank()) {

            JOptionPane.showMessageDialog(null,
                    "Todos os campos devem ser preenchidos antes de atualizar o restaurante.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE Restaurante SET nome=?, tipo_cozinha=?, telefone=?, endereco=? WHERE id_restaurante=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getNome());
            ps.setString(2, r.getTipoCozinha());
            ps.setString(3, r.getTelefone());
            ps.setString(4, r.getEndereco());
            ps.setInt(5, r.getIdRestaurante());
            ps.executeUpdate();
        }

        JOptionPane.showMessageDialog(null,
                "Restaurante atualizado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Restaurante WHERE id_restaurante = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

        JOptionPane.showMessageDialog(null,
                "Restaurante excluído com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private Restaurante map(ResultSet rs) throws SQLException {
        Restaurante r = new Restaurante();
        r.setIdRestaurante(rs.getInt("id_restaurante"));
        r.setNome(rs.getString("nome"));
        r.setTipoCozinha(rs.getString("tipo_cozinha"));
        r.setTelefone(rs.getString("telefone"));
        r.setEndereco(rs.getString("endereco"));
        return r;
    }
}
