package Delivery.ui;

import Delivery.dao.ItemPedidoDAO;
import Delivery.dao.PedidoDAO;
import Delivery.modelo.ItemPedido;
import Delivery.modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PedidoDialog extends JDialog {
    private final int idCliente;
    private final int idRestaurante;
    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemDAO;
    private final Runnable onFinish;

    private final JTable tabelaItens = new JTable(
            new DefaultTableModel(new Object[]{"Descrição","Qtd","Preço","Observação"}, 0)
    );

    public PedidoDialog(Frame owner, int idCliente, int idRestaurante,
                        PedidoDAO pedidoDAO, ItemPedidoDAO itemDAO, Runnable onFinish) {
        super(owner, "Novo Pedido", true);
        this.idCliente = idCliente;
        this.idRestaurante = idRestaurante;
        this.pedidoDAO = pedidoDAO;
        this.itemDAO = itemDAO;
        this.onFinish = onFinish;

        setSize(640, 420);
        setLocationRelativeTo(owner);
        montarUI();
    }

    private void montarUI() {
        JPanel centro = new JPanel(new BorderLayout(6,6));
        centro.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        centro.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField desc = new JTextField(18);
        JSpinner qtd = new JSpinner(new SpinnerNumberModel(1,1,999,1));
        JTextField preco = new JTextField(8);
        JTextField obs = new JTextField(12);
        JButton add = new JButton("Adicionar");
        form.add(new JLabel("Descrição:")); form.add(desc);
        form.add(new JLabel("Qtd:")); form.add(qtd);
        form.add(new JLabel("Preço:")); form.add(preco);
        form.add(new JLabel("Obs:")); form.add(obs);
        form.add(add);
        centro.add(form, BorderLayout.NORTH);

        add.addActionListener(e -> {
            try {
                BigDecimal p = new BigDecimal(preco.getText().replace(",", "."));
                ((DefaultTableModel)tabelaItens.getModel()).addRow(
                        new Object[]{desc.getText(), ((Number)qtd.getValue()).intValue(), p, obs.getText()}
                );
                desc.setText(""); qtd.setValue(1); preco.setText(""); obs.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Preço inválido.");
            }
        });

        JButton concluir = new JButton("Concluir Pedido");
        concluir.addActionListener(e -> salvarPedido());
        JPanel sul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sul.add(concluir);

        setLayout(new BorderLayout());
        add(centro, BorderLayout.CENTER);
        add(sul, BorderLayout.SOUTH);
    }

    private void salvarPedido() {
        try {
            // cria pedido com total 0
            Pedido p = new Pedido();
            p.setIdCliente(idCliente);
            p.setIdRestaurante(idRestaurante);
            p.setIdEntregador(null);
            p.setStatus("pendente");
            p.setValorTotal(BigDecimal.ZERO);
            p = pedidoDAO.inserir(p);

            BigDecimal total = BigDecimal.ZERO;
            DefaultTableModel m = (DefaultTableModel) tabelaItens.getModel();
            for (int i=0; i<m.getRowCount(); i++) {
                String d = (String) m.getValueAt(i, 0);
                int q = (int) m.getValueAt(i, 1);
                BigDecimal pr = (BigDecimal) m.getValueAt(i, 2);
                String ob = (String) m.getValueAt(i, 3);
                itemDAO.inserir(new ItemPedido(null, p.getIdPedido(), d, q, pr, ob));
                total = total.add(pr.multiply(BigDecimal.valueOf(q)));
            }

            // atualiza o total
            try (Connection con = Delivery.config.Conexao.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE Pedido SET valor_total=? WHERE id_pedido=?")) {
                ps.setBigDecimal(1, total);
                ps.setInt(2, p.getIdPedido());
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Pedido #" + p.getIdPedido() + " criado. Total: R$ " + total);
            if (onFinish != null) onFinish.run();
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
