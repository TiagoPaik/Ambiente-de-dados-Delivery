package Delivery.ui;

import Delivery.dao.ItemPedidoDAO;
import Delivery.modelo.ItemPedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProdutoDialog extends JDialog {
    private final int idRestaurante;
    private final ItemPedidoDAO itemDAO = new ItemPedidoDAO();
    private final JTable tabela = new JTable(new DefaultTableModel(
            new Object[]{"ID","Descrição","Quantidade","Preço","Observação"}, 0));

    public ProdutoDialog(Frame owner, int idRestaurante) {
        super(owner, "Cardápio do Restaurante #" + idRestaurante, true);
        this.idRestaurante = idRestaurante;
        setSize(800, 520);
        setLocationRelativeTo(owner);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton novo = new JButton("Novo");
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        JButton atualizar = new JButton("Atualizar");
        top.add(novo); top.add(editar); top.add(excluir); top.add(atualizar);

        novo.addActionListener(e -> abrirForm(null));
        editar.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um item."); return; }
            ItemPedido p = linhaParaItem(row);
            abrirForm(p);
        });
        excluir.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um item."); return; }
            int id = (int) tabela.getValueAt(row, 0);
            try {
                itemDAO.excluir(id);
                ((DefaultTableModel)tabela.getModel()).removeRow(row);
            } catch (SQLException ex) { showError(ex); }
        });
        atualizar.addActionListener(e -> carregar());

        setLayout(new BorderLayout(8,8));
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        carregar();
    }

    private void carregar() {
        try {
            DefaultTableModel m = (DefaultTableModel) tabela.getModel();
            m.setRowCount(0);
            List<ItemPedido> lista = itemDAO.listarPorRestaurante(idRestaurante);
            for (ItemPedido p : lista) {
                m.addRow(new Object[]{
                        p.getIdItem(),
                        p.getDescricao(),
                        p.getQuantidade(),
                        p.getPreco(),
                        p.getObservacao()
                });
            }
        } catch (SQLException e) { showError(e); }
    }

    private void abrirForm(ItemPedido existente) {
        JTextField descricao = new JTextField(18);
        JSpinner quantidade = new JSpinner(new SpinnerNumberModel(1,1,999,1));
        JTextField preco = new JTextField(8);
        JTextField obs = new JTextField(20);

        if (existente != null) {
            descricao.setText(existente.getDescricao());
            quantidade.setValue(existente.getQuantidade() == null ? 1 : existente.getQuantidade());
            preco.setText(existente.getPreco() == null ? "" : existente.getPreco().toPlainString());
            obs.setText(existente.getObservacao());
        }

        JPanel form = new JPanel(new GridBagLayout());
        var c = new GridBagConstraints(); c.insets=new Insets(6,6,6,6); c.anchor=GridBagConstraints.LINE_END;
        int y=0;
        c.gridx=0;c.gridy=y; form.add(new JLabel("Descrição:"), c);
        c.gridx=1;c.anchor=GridBagConstraints.LINE_START; form.add(descricao, c); y++;
        c.gridx=0;c.gridy=y;c.anchor=GridBagConstraints.LINE_END; form.add(new JLabel("Quantidade:"), c);
        c.gridx=1;c.anchor=GridBagConstraints.LINE_START; form.add(quantidade, c); y++;
        c.gridx=0;c.gridy=y;c.anchor=GridBagConstraints.LINE_END; form.add(new JLabel("Preço:"), c);
        c.gridx=1;c.anchor=GridBagConstraints.LINE_START; form.add(preco, c); y++;
        c.gridx=0;c.gridy=y;c.anchor=GridBagConstraints.LINE_END; form.add(new JLabel("Observação:"), c);
        c.gridx=1;c.anchor=GridBagConstraints.LINE_START; form.add(obs, c);

        int opt = JOptionPane.showConfirmDialog(this, form,
                (existente==null?"Novo Item":"Editar Item"), JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                BigDecimal valor = new BigDecimal(preco.getText().trim().replace(",", "."));
                int qtd = ((Number)quantidade.getValue()).intValue();

                if (existente == null) {
                    ItemPedido novo = new ItemPedido();
                    itemDAO.inserir(novo);
                } else {
                    existente.setDescricao(descricao.getText().trim());
                    existente.setQuantidade(qtd);
                    existente.setPreco(valor);
                    existente.setObservacao(obs.getText().trim());
                    itemDAO.atualizar(existente);
                }
                carregar();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private ItemPedido linhaParaItem(int row) {
        ItemPedido p = new ItemPedido();
        p.setIdItem((Integer) tabela.getValueAt(row,0));
        p.setIdRestaurante(idRestaurante);
        p.setDescricao((String) tabela.getValueAt(row,1));
        p.setQuantidade((Integer) tabela.getValueAt(row,2));
        // preço pode estar como BigDecimal ou String dependendo do TableModel
        Object precoObj = tabela.getValueAt(row,3);
        if (precoObj instanceof BigDecimal bd) p.setPreco(bd);
        else p.setPreco(new BigDecimal(String.valueOf(precoObj)));
        p.setObservacao((String) tabela.getValueAt(row,4));
        return p;
    }

    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(),"Erro", JOptionPane.ERROR_MESSAGE);
    }
}
