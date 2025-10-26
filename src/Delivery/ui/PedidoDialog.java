package Delivery.ui;

import Delivery.dao.ItemPedidoDAO;
import Delivery.dao.PedidoDAO;
import Delivery.modelo.ItemPedido;
import Delivery.modelo.Pedido;
import Delivery.ui.tabelas.ModeloTabelaNaoEditavel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDialog extends JDialog {

    private final int idCliente;
    private final int idRestaurante;
    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemDAO;
    private final Runnable callbackPosSalvar;

    private final List<ItemPedido> cardapioCarregado = new ArrayList<>();
    private final List<LinhaPedido> carrinho = new ArrayList<>();

    private final JTable tabelaCardapio = new JTable(
            new ModeloTabelaNaoEditavel(new Object[]{"Descrição","Preço","Observação"}, 0) {
                @Override public Class<?> getColumnClass(int columnIndex) {
                    return switch (columnIndex) { case 1 -> BigDecimal.class; default -> String.class; };
                }
            }
    );
    private final JTable tabelaCarrinho = new JTable(
            new ModeloTabelaNaoEditavel(new Object[]{"Descrição","Preço","Qtd","Subtotal"}, 0) {
                @Override public Class<?> getColumnClass(int columnIndex) {
                    return switch (columnIndex) { case 1,3 -> BigDecimal.class; case 2 -> Integer.class; default -> String.class; };
                }
            }
    );

    private ModeloTabelaNaoEditavel modeloCardapio() { return (ModeloTabelaNaoEditavel) tabelaCardapio.getModel(); }
    private ModeloTabelaNaoEditavel modeloCarrinho() { return (ModeloTabelaNaoEditavel) tabelaCarrinho.getModel(); }

    private final JLabel lblTotal = new JLabel("Total: R$ 0,00");

    public PedidoDialog(Frame owner, int idCliente, int idRestaurante,
                        PedidoDAO pedidoDAO, ItemPedidoDAO itemDAO, Runnable callbackPosSalvar) {
        super(owner, "Novo Pedido - Restaurante #" + idRestaurante, true);
        this.idCliente = idCliente;
        this.idRestaurante = idRestaurante;
        this.pedidoDAO = pedidoDAO;
        this.itemDAO = itemDAO;
        this.callbackPosSalvar = callbackPosSalvar;

        setSize(1000, 580);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8,8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnAtualizar = new JButton("Atualizar Cardápio");
        JButton btnAdicionar  = new JButton("Adicionar ao carrinho");
        JButton btnFinalizar  = new JButton("Finalizar compra...");
        top.add(btnAtualizar); top.add(btnAdicionar); top.add(btnFinalizar);
        add(top, BorderLayout.NORTH);

        JScrollPane spCardapio  = new JScrollPane(tabelaCardapio);
        JScrollPane spCarrinho  = new JScrollPane(tabelaCarrinho);
        JPanel right = new JPanel(new BorderLayout(8,8));

        JPanel barraCarrinho   = new JPanel(new BorderLayout(8,8));
        JPanel botoesCarrinho  = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnRemover     = new JButton("Remover do carrinho");
        JButton btnAlterarQtd  = new JButton("Alterar quantidade");
        JButton btnLimpar      = new JButton("Limpar carrinho");
        botoesCarrinho.add(btnRemover); botoesCarrinho.add(btnAlterarQtd); botoesCarrinho.add(btnLimpar);
        barraCarrinho.add(botoesCarrinho, BorderLayout.WEST);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        totalPanel.add(lblTotal);
        barraCarrinho.add(totalPanel, BorderLayout.EAST);

        right.add(new JLabel("Carrinho"), BorderLayout.NORTH);
        right.add(spCarrinho, BorderLayout.CENTER);
        right.add(barraCarrinho, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spCardapio, right);
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);

        // Listeners
        btnAtualizar.addActionListener(e -> carregarCardapio());
        btnAdicionar.addActionListener(e -> adicionarSelecionadosAoCarrinho());
        btnRemover.addActionListener(e -> removerSelecionadoDoCarrinho());
        btnAlterarQtd.addActionListener(e -> alterarQuantidadeSelecionada());
        btnLimpar.addActionListener(e -> limparCarrinho());
        btnFinalizar.addActionListener(e -> finalizarCompra());

        tabelaCardapio.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabelaCarrinho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaCarrinho.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tabelaCarrinho.getSelectedRow() >= 0) alterarQuantidadeSelecionada();
            }
        });

        carregarCardapio();
        atualizarTotal();
    }

    // ===== Fluxo do cardápio/carrinho
    private void carregarCardapio() {
        try {
            cardapioCarregado.clear();
            modeloCardapio().setRowCount(0);
            List<ItemPedido> lista = itemDAO.listarPorRestaurante(idRestaurante);
            for (ItemPedido it : lista) {
                cardapioCarregado.add(it);
                modeloCardapio().addRow(new Object[]{ it.getDescricao(), it.getPreco(), it.getObservacao() });
            }
        } catch (SQLException e) { erro(e); }
    }

    private void adicionarSelecionadosAoCarrinho() {
        int[] viewRows = tabelaCardapio.getSelectedRows();
        if (viewRows.length == 0) { JOptionPane.showMessageDialog(this, "Selecione 1 ou mais itens."); return; }
        for (int vr : viewRows) {
            int mr = tabelaCardapio.convertRowIndexToModel(vr);
            ItemPedido item = cardapioCarregado.get(mr);
            String desc = item.getDescricao();
            BigDecimal preco = item.getPreco();

            String resp = JOptionPane.showInputDialog(this, "Quantidade para: " + desc, "1");
            if (resp == null) continue;
            int qtd;
            try { qtd = Math.max(1, Integer.parseInt(resp.trim())); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this,"Quantidade inválida para \""+desc+"\"."); continue; }

            LinhaPedido lp = new LinhaPedido(item.getIdItem(), desc, preco, qtd);
            carrinho.add(lp);

            BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(qtd));
            modeloCarrinho().addRow(new Object[]{ desc, preco, qtd, subtotal });
        }
        atualizarTotal();
    }

    private void removerSelecionadoDoCarrinho() {
        int r = tabelaCarrinho.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Selecione um item no carrinho."); return; }
        carrinho.remove(r);
        modeloCarrinho().removeRow(r);
        atualizarTotal();
    }

    private void alterarQuantidadeSelecionada() {
        int r = tabelaCarrinho.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Selecione um item no carrinho."); return; }
        String desc  = String.valueOf(modeloCarrinho().getValueAt(r, 0));
        String atual = String.valueOf(modeloCarrinho().getValueAt(r, 2));
        String resp  = JOptionPane.showInputDialog(this, "Nova quantidade para: " + desc, atual);
        if (resp == null) return;

        int qtd;
        try { qtd = Math.max(1, Integer.parseInt(resp.trim())); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this,"Quantidade inválida."); return; }

        LinhaPedido lp = carrinho.get(r);
        lp.qtd = qtd;

        BigDecimal subtotal = lp.preco.multiply(BigDecimal.valueOf(qtd));
        modeloCarrinho().setValueAt(qtd, r, 2);
        modeloCarrinho().setValueAt(subtotal, r, 3);
        atualizarTotal();
    }

    private void limparCarrinho() {
        if (modeloCarrinho().getRowCount() == 0) return;
        if (JOptionPane.showConfirmDialog(this, "Limpar todo o carrinho?", "Confirmar",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            carrinho.clear();
            modeloCarrinho().setRowCount(0);
            atualizarTotal();
        }
        ;
    }

    private void atualizarTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < modeloCarrinho().getRowCount(); i++) {
            Object subObj = modeloCarrinho().getValueAt(i, 3);
            BigDecimal sub = (subObj instanceof BigDecimal bd) ? bd : new BigDecimal(String.valueOf(subObj));
            total = total.add(sub);
        }
        lblTotal.setText("Total: R$ " + total);
    }

    // ===== Finalização e gravação (com estoque)
    private void finalizarCompra() {
        if (carrinho.isEmpty()) { JOptionPane.showMessageDialog(this, "O carrinho está vazio."); return; }

        StringBuilder resumo = new StringBuilder("Itens do pedido:\n\n");
        BigDecimal total = BigDecimal.ZERO;
        for (LinhaPedido lp : carrinho) {
            BigDecimal sub = lp.preco.multiply(BigDecimal.valueOf(lp.qtd));
            total = total.add(sub);
            resumo.append(String.format("- %s  x%d   (R$ %s)\n", lp.descricao, lp.qtd, sub));
        }
        resumo.append("\nTOTAL: R$ ").append(total);

        // 0) Checagem de disponibilidade (UX)
        try {
            String faltas = itemDAO.verificarDisponibilidade(carrinho);
            if (faltas != null) {
                JOptionPane.showMessageDialog(this,
                        "Itens indisponíveis ou com estoque insuficiente:\n\n" + faltas,
                        "Estoque insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException e) { erro(e); return; }

        int ok = JOptionPane.showConfirmDialog(this, resumo.toString(),
                "Confirmar pedido", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            // Transação: pedido + itens + baixa
            Pedido p = pedidoDAO.criarPedidoComItens(idCliente, idRestaurante, carrinho, total, itemDAO);

            JOptionPane.showMessageDialog(this, "Pedido criado! ID: " + p.getIdPedido());
            if (callbackPosSalvar != null) { try { callbackPosSalvar.run(); } catch (Exception ignored) {} }
            dispose();
        } catch (SQLException e) { erro(e); }
    }

    private void erro(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // DTO simples para linhas do pedido (ID fica interno)
    public static class LinhaPedido {
        public final int idItem;
        public final String descricao;
        public final BigDecimal preco;
        public int qtd;

        public LinhaPedido(int idItem, String descricao, BigDecimal preco, int qtd) {
            this.idItem = idItem;
            this.descricao = descricao;
            this.preco = preco;
            this.qtd = qtd;
        }
    }
}
