package Delivery.ui;

import Delivery.config.Conexao;
import Delivery.dao.ItemPedidoDAO;
import Delivery.modelo.ItemPedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class PedidoDetalheDialog extends JDialog {
    private final ItemPedidoDAO itemDAO = new ItemPedidoDAO();
    private final JTable tabela = new JTable(new DefaultTableModel(
            new Object[]{"ID Item","Descrição","Qtd","Preço","Obs"}, 0));

    public PedidoDetalheDialog(Frame owner, int idPedido) {
        super(owner, "Itens (cardápio) do restaurante do Pedido #" + idPedido, true);
        setSize(700, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8,8));
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        carregarPorPedido(idPedido);
    }

    /** Busca o id_restaurante do pedido e lista os itens do restaurante (cardápio) */
    private void carregarPorPedido(int idPedido) {
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id_restaurante FROM Pedido WHERE id_pedido=?")) {
            ps.setInt(1, idPedido);
            Integer idRest = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idRest = rs.getInt("id_restaurante");
            }
            if (idRest == null) {
                JOptionPane.showMessageDialog(this, "Pedido não encontrado.");
                return;
            }
            carregarItensDoRestaurante(idRest);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarItensDoRestaurante(int idRestaurante) {
        try {
            var m = (DefaultTableModel) tabela.getModel();
            m.setRowCount(0);
            List<ItemPedido> itens = itemDAO.listarPorRestaurante(idRestaurante);
            for (ItemPedido it : itens) {
                m.addRow(new Object[]{
                        it.getIdItem(), it.getDescricao(), it.getQuantidade(),
                        it.getPreco(), it.getObservacao()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
