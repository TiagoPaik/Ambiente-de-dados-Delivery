package Delivery.dao;

import Delivery.modelo.Entregador;
import util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregadorDAO {

    public List<Entregador> listar() throws SQLException {
        String sql = "SELECT id_entregador, nome, status, veiculo FROM Entregador ORDER BY nome";
        List<Entregador> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    public Entregador buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_entregador, nome, status, veiculo FROM Entregador WHERE id_entregador = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public Entregador inserir(Entregador e) throws SQLException {
        String sql = "INSERT INTO Entregador (nome, status, veiculo) VALUES (?,?,?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getStatus());
            ps.setString(3, e.getVeiculo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setIdEntregador(rs.getInt(1));
            }
        }
        return e;
    }

    public void atualizar(Entregador e) throws SQLException {
        String sql = "UPDATE Entregador SET nome=?, status=?, veiculo=? WHERE id_entregador = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getStatus());
            ps.setString(3, e.getVeiculo());
            ps.setInt(4, e.getIdEntregador());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM Entregador WHERE id_entregador = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void atualizarStatus(int idEntregador, String status) throws SQLException {
        String sql = "UPDATE Entregador SET status=? WHERE id_entregador=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idEntregador);
            ps.executeUpdate();
        }
    }

    private Entregador map(ResultSet rs) throws SQLException {
        Entregador e = new Entregador();
        e.setIdEntregador(rs.getInt("id_entregador"));
        e.setNome(rs.getString("nome"));
        e.setStatus(rs.getString("status"));
        e.setVeiculo(rs.getString("veiculo"));
        return e;
    }
}
