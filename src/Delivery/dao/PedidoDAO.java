package Delivery.dao;

import Delivery.modelo.Pedido;
import util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    // ===== Listagens / CRUD básico =====
    public List<Pedido> listarTodos() throws SQLException {
        String sql = """
            SELECT id_pedido, id_cliente, id_restaurante, id_entregador, data_hora, status, valor_total
            FROM Pedido
            ORDER BY data_hora DESC
        """;
        List<Pedido> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    public List<Pedido> listarPorCliente(int idCliente) throws SQLException {
        String sql = """
            SELECT id_pedido, id_cliente, id_restaurante, id_entregador, data_hora, status, valor_total
            FROM Pedido
            WHERE id_cliente = ?
            ORDER BY data_hora DESC
        """;
        List<Pedido> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public Pedido buscarPorId(int idPedido) throws SQLException {
        String sql = """
            SELECT id_pedido, id_cliente, id_restaurante, id_entregador, data_hora, status, valor_total
            FROM Pedido
            WHERE id_pedido = ?
        """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void inserir(Pedido p) throws SQLException {
        String sql = """
            INSERT INTO Pedido (id_cliente, id_restaurante, id_entregador, data_hora, status, valor_total)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdRestaurante());
            if (p.getIdEntregador() == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, p.getIdEntregador());
            ps.setTimestamp(4, Timestamp.valueOf(p.getDataHora() != null ? p.getDataHora() : LocalDateTime.now()));
            ps.setString(5, p.getStatus());
            ps.setBigDecimal(6, p.getValorTotal() != null ? p.getValorTotal() : BigDecimal.ZERO);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setIdPedido(rs.getInt(1));
            }
        }
    }

    public void atualizarStatus(int idPedido, String status) throws SQLException {
        String sql = "UPDATE Pedido SET status = ? WHERE id_pedido = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public void atribuirEntregador(int idPedido, Integer idEntregador) throws SQLException {
        String sql = "UPDATE Pedido SET id_entregador = ? WHERE id_pedido = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (idEntregador == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, idEntregador);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public void cancelarPedido(int idPedido) throws SQLException {
        String sql = "UPDATE Pedido SET status = 'cancelado' WHERE id_pedido = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.executeUpdate();
        }
    }

    // ===== Transacional: cria pedido + itens + baixa estoque =====
    public Pedido criarPedidoComItens(int idCliente, int idRestaurante,
                                      List<Delivery.ui.PedidoDialog.LinhaPedido> linhas,
                                      BigDecimal total,
                                      ItemPedidoDAO itemDAO) throws SQLException {
        // 🚨 Verificação inicial — impede criar pedido sem itens
        if (linhas == null || linhas.isEmpty()) {
            throw new SQLException("Não é possível criar um pedido sem itens.");
        }

        String sqlPedido = """
        INSERT INTO Pedido (id_cliente, id_restaurante, id_entregador, data_hora, status, valor_total)
        VALUES (?, ?, NULL, NOW(), 'pendente', ?)
    """;
        String sqlItens = """
        INSERT INTO pedido_itens (id_pedido, id_item, descricao, preco_unit, quantidade, subtotal)
        VALUES (?,?,?,?,?,?)
    """;

        try (Connection c = ConnectionFactory.getConnection()) {
            boolean oldAuto = c.getAutoCommit();
            c.setAutoCommit(false);
            try (PreparedStatement psPed = c.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psIt = c.prepareStatement(sqlItens)) {

                // 1) Pedido
                psPed.setInt(1, idCliente);
                psPed.setInt(2, idRestaurante);
                psPed.setBigDecimal(3, total);
                psPed.executeUpdate();
                int idPedido;
                try (ResultSet rs = psPed.getGeneratedKeys()) {
                    rs.next();
                    idPedido = rs.getInt(1);
                }

                // 2) Itens
                for (var lp : linhas) {
                    BigDecimal sub = lp.preco.multiply(BigDecimal.valueOf(lp.qtd));
                    psIt.setInt(1, idPedido);
                    psIt.setInt(2, lp.idItem);
                    psIt.setString(3, lp.descricao);
                    psIt.setBigDecimal(4, lp.preco);
                    psIt.setInt(5, lp.qtd);
                    psIt.setBigDecimal(6, sub);
                    psIt.addBatch();
                }
                psIt.executeBatch();

                // 3) Baixa no estoque (com trava de quantidade)
                boolean ok = itemDAO.baixarEstoqueEmLote(c, linhas);
                if (!ok) {
                    c.rollback();
                    throw new SQLException("Estoque insuficiente no momento da confirmação.");
                }

                c.commit();

                Pedido p = new Pedido();
                p.setIdPedido(idPedido);
                p.setIdCliente(idCliente);
                p.setIdRestaurante(idRestaurante);
                p.setStatus("pendente");
                p.setValorTotal(total);
                p.setDataHora(LocalDateTime.now());
                return p;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(oldAuto);
            }
        }
    }


    // ===== Mapper =====
    private Pedido map(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getInt("id_pedido"));
        p.setIdCliente(rs.getInt("id_cliente"));
        p.setIdRestaurante(rs.getInt("id_restaurante"));
        int idEnt = rs.getInt("id_entregador");
        p.setIdEntregador(rs.wasNull() ? null : idEnt);
        Timestamp ts = rs.getTimestamp("data_hora");
        p.setDataHora(ts != null ? ts.toLocalDateTime() : null);
        p.setStatus(rs.getString("status"));
        p.setValorTotal(rs.getBigDecimal("valor_total"));
        return p;
    }
}
