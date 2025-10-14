package Delivery.dao;

import Delivery.config.Conexao;
import Delivery.modelo.ItemPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemPedidoDAO {

    public ItemPedido inserir(ItemPedido i) throws SQLException {
        String sql = "INSERT INTO ItemPedido (id_pedido, descricao, quantidade, preco, observacao) VALUES (?,?,?,?,?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, i.getIdPedido());
            ps.setString(2, i.getDescricao());
            ps.setInt(3, i.getQuantidade());
            ps.setBigDecimal(4, i.getPreco());
            ps.setString(5, i.getObservacao());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) i.setIdItem(rs.getInt(1));
            }
            return i;
        }
    }

    public List<ItemPedido> listarPorPedido(int idPedido) throws SQLException {
        String sql = "SELECT * FROM ItemPedido WHERE id_pedido=?";
        List<ItemPedido> itens = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemPedido i = new ItemPedido(
                            rs.getInt("id_item"),
                            rs.getInt("id_pedido"),
                            rs.getString("descricao"),
                            rs.getInt("quantidade"),
                            rs.getBigDecimal("preco"),
                            rs.getString("observacao")
                    );
                    itens.add(i);
                }
            }
        }
        return itens;
    }
}
