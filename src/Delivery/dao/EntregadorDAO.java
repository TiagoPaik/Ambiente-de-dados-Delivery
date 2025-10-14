package Delivery.dao;

import Delivery.config.Conexao;
import Delivery.modelo.Entregador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregadorDAO {

    public Entregador inserir(Entregador e) throws SQLException {
        String sql = "INSERT INTO Entregador (nome, status, veiculo) VALUES (?,?,?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNome());
            ps.setString(2, e.getStatus());
            ps.setString(3, e.getVeiculo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setIdEntregador(rs.getInt(1));
            }
            return e;
        }
    }

    public List<Entregador> listarDisponiveis() throws SQLException {
        String sql = "SELECT * FROM Entregador WHERE status='disponivel'";
        List<Entregador> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Entregador(
                        rs.getInt("id_entregador"),
                        rs.getString("nome"),
                        rs.getString("status"),
                        rs.getString("veiculo")
                ));
            }
        }
        return lista;
    }
}
