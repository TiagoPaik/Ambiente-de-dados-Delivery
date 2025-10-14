package Delivery.dao;

import Delivery.config.Conexao;
import Delivery.modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO Cliente (nome, telefone, endereco, email, senha, cpf) VALUES (?,?,?,?,?,?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNome());
            ps.setString(2, c.getTelefone());
            ps.setString(3, c.getEndereco());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getSenha());
            ps.setString(6, c.getCpf());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCliente(rs.getInt(1));
            }
            return c;
        }
    }

    public Cliente buscarPorEmailSenha(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE email=? AND senha=?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT * FROM Cliente";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    private Cliente map(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNome(rs.getString("nome"));
        c.setTelefone(rs.getString("telefone"));
        c.setEndereco(rs.getString("endereco"));
        c.setEmail(rs.getString("email"));
        c.setSenha(rs.getString("senha"));
        c.setCpf(rs.getString("cpf"));
        return c;
    }
}
