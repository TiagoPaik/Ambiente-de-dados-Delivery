package Delivery.dao;

import Delivery.config.Conexao;
import Delivery.modelo.Restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestauranteDAO {

    public Restaurante inserir(Restaurante r) throws SQLException {
        String sql = "INSERT INTO Restaurante (nome, tipo_cozinha, telefone, endereco) VALUES (?,?,?,?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNome());
            ps.setString(2, r.getTipoCozinha());
            ps.setString(3, r.getTelefone());
            ps.setString(4, r.getEndereco());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setIdRestaurante(rs.getInt(1));
            }
            return r;
        }
    }

    public List<Restaurante> listar() throws SQLException {
        String sql = "SELECT * FROM Restaurante";
        List<Restaurante> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Restaurante(
                        rs.getInt("id_restaurante"),
                        rs.getString("nome"),
                        rs.getString("tipo_cozinha"),
                        rs.getString("telefone"),
                        rs.getString("endereco")
                ));
            }
        }
        return lista;
    }
}
