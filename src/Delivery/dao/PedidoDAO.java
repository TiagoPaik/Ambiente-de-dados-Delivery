package Delivery.dao;

import Delivery.config.Conexao;
import Delivery.modelo.Pedido;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public Pedido inserir(Pedido p) throws SQLException {
        String sql = "INSERT INTO Pedido (id_cliente, id_restaurante, id_entregador, status, valor_total) VALUES (?,?,?,?,?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdRestaurante());
            if (p.getIdEntregador() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, p.getIdEntregador());
            ps.setString(4, p.getStatus());
            ps.setBigDecimal(5, p.getValorTotal() == null ? BigDecimal.ZERO : p.getValorTotal());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setIdPedido(rs.getInt(1));
            }
            return p;
        }
    }

    public void atualizarStatus(int idPedido, String novoStatus) throws SQLException {
        String sql = "UPDATE Pedido SET status=? WHERE id_pedido=?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, novoStatus);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public List<Pedido> listarPorCliente(int idCliente) throws SQLException {
        String sql = "SELECT * FROM Pedido WHERE id_cliente=? ORDER BY data_hora DESC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        }
        return lista;
    }

    private Pedido map(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getInt("id_pedido"));
        p.setIdCliente(rs.getInt("id_cliente"));
        p.setIdRestaurante(rs.getInt("id_restaurante"));
        int ide = rs.getInt("id_entregador");
        p.setIdEntregador(rs.wasNull() ? null : ide);
        Timestamp ts = rs.getTimestamp("data_hora");
        p.setDataHora(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        p.setStatus(rs.getString("status"));
        p.setValorTotal(rs.getBigDecimal("valor_total"));
        return p;
    }
}
