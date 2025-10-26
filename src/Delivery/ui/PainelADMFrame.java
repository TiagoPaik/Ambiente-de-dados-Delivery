package Delivery.ui;

import Delivery.dao.EntregadorDAO;
import Delivery.dao.ItemPedidoDAO;
import Delivery.dao.PedidoDAO;
import Delivery.dao.RestauranteDAO;
import Delivery.modelo.Entregador;
import Delivery.modelo.ItemPedido;
import Delivery.modelo.Pedido;
import Delivery.modelo.Restaurante;
import Delivery.ui.tabelas.ModeloTabelaNaoEditavel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PainelADMFrame extends JFrame {

    private final RestauranteDAO restauranteDAO = new RestauranteDAO();
    private final EntregadorDAO entregadorDAO  = new EntregadorDAO();
    private final PedidoDAO pedidoDAO          = new PedidoDAO();
    private final ItemPedidoDAO itemDAO        = new ItemPedidoDAO();

    private final JTable tabelaRestaurantes = new JTable(new ModeloTabelaNaoEditavel(
            new Object[]{"ID","Nome","Tipo","Telefone","Endereço"}, 0));
    private final JTable tabelaEntregadores = new JTable(new ModeloTabelaNaoEditavel(
            new Object[]{"ID","Nome","Status","Veículo"}, 0));

    private final JComboBox<Restaurante> comboRestCardapio = new JComboBox<>();
    private final JTable tabelaCardapio = new JTable(
            new ModeloTabelaNaoEditavel(new Object[]{"ID","Descrição","Preço","Estoque","Obs"}, 0)
    );

    private final String[] STATUS_TABS = {"pendente", "em_preparo", "enviado", "entregue", "cancelado"};
    private JTabbedPane tabsPedidos;
    private final Map<String, JTable> tabelasPorStatus = new LinkedHashMap<>();

    public PainelADMFrame() {
        super("Painel do Administrador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JButton btnLogout = new JButton("Sair / Logout");
        btnLogout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(new JLabel("Administrador"));
        top.add(btnLogout);
        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Restaurantes", tabRestaurantes());
        tabs.addTab("Entregadores", tabEntregadores());
        tabs.addTab("Cardápio", tabCardapio());
        tabs.addTab("Pedidos", tabPedidos());
        add(tabs, BorderLayout.CENTER);

        carregarRestaurantes();
        carregarEntregadores();
        carregarComboCardapio();
        carregarCardapioAdm();
        carregarPedidosPorStatus();
    }


    private JPanel tabRestaurantes() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        p.add(new JScrollPane(tabelaRestaurantes), BorderLayout.CENTER);

        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");

        btnNovo.addActionListener(e -> abrirDialogRestaurante(null));
        btnEditar.addActionListener(e -> {
            int r = tabelaRestaurantes.getSelectedRow();
            if (r < 0) { msg("Selecione um restaurante."); return; }
            int id = (int) tabelaRestaurantes.getValueAt(r, 0);
            try { abrirDialogRestaurante(restauranteDAO.buscarPorId(id)); }
            catch (SQLException ex) { showError(ex); }
        });
        btnExcluir.addActionListener(e -> {
            int r = tabelaRestaurantes.getSelectedRow();
            if (r < 0) { msg("Selecione um restaurante."); return; }
            int id = (int) tabelaRestaurantes.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this, "Excluir restaurante "+id+"?", "Confirmar",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    restauranteDAO.excluir(id);
                    carregarRestaurantes();
                    carregarComboCardapio();
                    carregarCardapioAdm();
                } catch (SQLException ex) { showError(ex); }
            }
        });
        btnAtualizar.addActionListener(e -> {
            carregarRestaurantes();
            carregarComboCardapio();
            carregarCardapioAdm();
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnNovo); south.add(btnEditar); south.add(btnExcluir); south.add(btnAtualizar);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void abrirDialogRestaurante(Restaurante existente) {
        JTextField nome = new JTextField(20);
        JTextField tipo = new JTextField(15);
        JTextField tel  = new JTextField(12);
        JTextField end  = new JTextField(22);

        if (existente != null) {
            nome.setText(existente.getNome());
            tipo.setText(existente.getTipoCozinha());
            tel.setText(existente.getTelefone());
            end.setText(existente.getEndereco());
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4); c.anchor = GridBagConstraints.WEST;
        int y=0;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Nome:"), c); c.gridx=1; panel.add(nome, c); y++;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Tipo Cozinha:"), c); c.gridx=1; panel.add(tipo, c); y++;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Telefone:"), c); c.gridx=1; panel.add(tel, c); y++;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Endereço:"), c); c.gridx=1; panel.add(end, c);

        int ok = JOptionPane.showConfirmDialog(this, panel,
                (existente==null? "Novo Restaurante":"Editar Restaurante"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            if (existente == null) {
                restauranteDAO.inserir(new Restaurante(null, nome.getText(), tipo.getText(), tel.getText(), end.getText()));
            } else {
                existente.setNome(nome.getText());
                existente.setTipoCozinha(tipo.getText());
                existente.setTelefone(tel.getText());
                existente.setEndereco(end.getText());
                restauranteDAO.atualizar(existente);
            }
            carregarRestaurantes();
            carregarComboCardapio();
            carregarCardapioAdm();
        } catch (SQLException ex) { showError(ex); }
    }

    private void carregarRestaurantes() {
        try {
            var m = (ModeloTabelaNaoEditavel) tabelaRestaurantes.getModel();
            m.setRowCount(0);
            for (Restaurante r : restauranteDAO.listar()) {
                m.addRow(new Object[]{ r.getIdRestaurante(), r.getNome(), r.getTipoCozinha(), r.getTelefone(), r.getEndereco() });
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private JPanel tabEntregadores() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        p.add(new JScrollPane(tabelaEntregadores), BorderLayout.CENTER);

        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnMudarStatus = new JButton("Mudar Status");

        btnNovo.addActionListener(e -> abrirDialogEntregador(null));
        btnEditar.addActionListener(e -> {
            int r = tabelaEntregadores.getSelectedRow();
            if (r < 0) { msg("Selecione um entregador."); return; }
            int id = (int) tabelaEntregadores.getValueAt(r, 0);
            try { abrirDialogEntregador(entregadorDAO.buscarPorId(id)); } catch (SQLException ex) { showError(ex); }
        });
        btnExcluir.addActionListener(e -> {
            int r = tabelaEntregadores.getSelectedRow();
            if (r < 0) { msg("Selecione um entregador."); return; }
            int id = (int) tabelaEntregadores.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this, "Excluir entregador "+id+"?", "Confirmar",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try { entregadorDAO.excluir(id); carregarEntregadores(); } catch (SQLException ex) { showError(ex); }
            }
        });
        btnAtualizar.addActionListener(e -> carregarEntregadores());
        btnMudarStatus.addActionListener(e -> {
            int r = tabelaEntregadores.getSelectedRow();
            if (r < 0) { msg("Selecione um entregador."); return; }
            int id = (int) tabelaEntregadores.getValueAt(r, 0);
            String[] sts = {"disponivel","ocupado","inativo"};
            String novo = (String) JOptionPane.showInputDialog(this, "Novo status:",
                    "Status do Entregador", JOptionPane.PLAIN_MESSAGE, null, sts, sts[0]);
            if (novo != null) {
                try { entregadorDAO.atualizarStatus(id, novo); carregarEntregadores(); } catch (SQLException ex) { showError(ex); }
            }
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnNovo); south.add(btnEditar); south.add(btnExcluir);
        south.add(btnMudarStatus); south.add(btnAtualizar);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void abrirDialogEntregador(Entregador existente) {
        JTextField nome = new JTextField(20);
        JTextField veiculo = new JTextField(15);
        JComboBox<String> status = new JComboBox<>(new String[]{"disponivel","ocupado","inativo"});

        if (existente != null) {
            nome.setText(existente.getNome());
            veiculo.setText(existente.getVeiculo());
            status.setSelectedItem(existente.getStatus());
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4); c.anchor = GridBagConstraints.WEST;
        int y=0;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Nome:"), c);    c.gridx=1; panel.add(nome, c); y++;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Veículo:"), c); c.gridx=1; panel.add(veiculo, c); y++;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Status:"), c);  c.gridx=1; panel.add(status, c);

        int ok = JOptionPane.showConfirmDialog(this, panel,
                (existente==null? "Novo Entregador":"Editar Entregador"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            if (existente == null) {
                entregadorDAO.inserir(new Entregador(null, nome.getText(), (String) status.getSelectedItem(), veiculo.getText()));
            } else {
                existente.setNome(nome.getText());
                existente.setVeiculo(veiculo.getText());
                existente.setStatus((String) status.getSelectedItem());
                entregadorDAO.atualizar(existente);
            }
            carregarEntregadores();
        } catch (SQLException ex) { showError(ex); }
    }

    private void carregarEntregadores() {
        try {
            var m = (ModeloTabelaNaoEditavel) tabelaEntregadores.getModel();
            m.setRowCount(0);
            for (Entregador e : entregadorDAO.listar()) {
                m.addRow(new Object[]{ e.getIdEntregador(), e.getNome(), e.getStatus(), e.getVeiculo() });
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private JPanel tabCardapio() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnAtualizarCombo = new JButton("Recarregar Restaurantes");
        top.add(new JLabel("Restaurante:"));
        top.add(comboRestCardapio);
        top.add(btnAtualizarCombo);

        comboRestCardapio.setRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Restaurante r) setText(r.getNome());
                return this;
            }
        });
        comboRestCardapio.addActionListener(e -> carregarCardapioAdm());
        btnAtualizarCombo.addActionListener(e -> { carregarComboCardapio(); carregarCardapioAdm(); });

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(tabelaCardapio), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnNovo = new JButton("Novo");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnRefresh = new JButton("Atualizar Itens");
        JButton btnRepor = new JButton("Repor Estoque...");
        south.add(btnNovo); south.add(btnEditar); south.add(btnExcluir); south.add(btnRepor); south.add(btnRefresh);
        p.add(south, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> abrirDialogItem(null));
        btnEditar.addActionListener(e -> {
            int r = tabelaCardapio.getSelectedRow();
            if (r < 0) { msg("Selecione um item."); return; }
            int idItem = (int) tabelaCardapio.getValueAt(r, 0);
            try { abrirDialogItem(itemDAO.buscarPorId(idItem)); } catch (SQLException ex) { showError(ex); }
        });
        btnExcluir.addActionListener(e -> {
            int r = tabelaCardapio.getSelectedRow();
            if (r < 0) { msg("Selecione um item."); return; }
            int idItem = (int) tabelaCardapio.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this, "Excluir item "+idItem+"?", "Confirmar",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try { itemDAO.excluir(idItem); carregarCardapioAdm(); } catch (SQLException ex) { showError(ex); }
            }
        });
        btnRefresh.addActionListener(e -> carregarCardapioAdm());
        btnRepor.addActionListener(e -> {
            int r = tabelaCardapio.getSelectedRow();
            if (r < 0) { msg("Selecione um item."); return; }
            int idItem = (int) tabelaCardapio.getValueAt(r, 0);
            String resp = JOptionPane.showInputDialog(this, "Quantidade a repor:", "10");
            if (resp == null) return;
            int qtd;
            try { qtd = Math.max(0, Integer.parseInt(resp.trim())); }
            catch (Exception ex) { msg("Quantidade inválida."); return; }
            try { itemDAO.reporEstoque(idItem, qtd); carregarCardapioAdm(); } catch (SQLException ex) { showError(ex); }
        });

        return p;
    }

    private void carregarComboCardapio() {
        try {
            comboRestCardapio.removeAllItems();
            for (Restaurante r : restauranteDAO.listar()) comboRestCardapio.addItem(r);
        } catch (SQLException ex) { showError(ex); }
    }

    private void carregarCardapioAdm() {
        try {
            var m = (ModeloTabelaNaoEditavel) tabelaCardapio.getModel();
            m.setRowCount(0);
            Restaurante sel = (Restaurante) comboRestCardapio.getSelectedItem();
            if (sel == null) return;
            for (ItemPedido it : itemDAO.listarPorRestaurante(sel.getIdRestaurante())) {
                m.addRow(new Object[]{ it.getIdItem(), it.getDescricao(),
                        it.getPreco(), it.getEstoque(), it.getObservacao() });
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void abrirDialogItem(ItemPedido existente) {
        Restaurante rest = (Restaurante) comboRestCardapio.getSelectedItem();
        if (rest == null) { msg("Selecione um restaurante."); return; }

        JTextField descricao = new JTextField(22);
        JFormattedTextField preco = new JFormattedTextField(java.text.NumberFormat.getNumberInstance());
        preco.setColumns(8);
        JTextField obs = new JTextField(22);
        JSpinner estoque = new JSpinner(new SpinnerNumberModel(0,0,100000,1)); // apenas estoque agora

        if (existente != null) {
            descricao.setText(existente.getDescricao());
            preco.setValue(existente.getPreco());
            obs.setText(existente.getObservacao());
            estoque.setValue(existente.getEstoque() == null ? 0 : existente.getEstoque());
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4); c.anchor = GridBagConstraints.WEST;
        int y=0;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Descrição:"), c);  c.gridx=1; panel.add(descricao, c); y++;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Preço:"), c);      c.gridx=1; panel.add(preco, c); y++;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Estoque:"), c);    c.gridx=1; panel.add(estoque, c); y++;
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Observação:"), c); c.gridx=1; panel.add(obs, c);

        int ok = JOptionPane.showConfirmDialog(this, panel,
                (existente==null? "Novo Item":"Editar Item"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            if (existente == null) {
                ItemPedido novo = new ItemPedido();
                novo.setIdRestaurante(rest.getIdRestaurante());
                novo.setDescricao(descricao.getText());
                novo.setQuantidade(1); // compatibilidade (não exibimos Qtd no cardápio)
                novo.setPreco(toBigDecimal(preco.getValue()));
                novo.setObservacao(obs.getText());
                novo.setEstoque((Integer) estoque.getValue());
                itemDAO.inserir(novo);
            } else {
                existente.setDescricao(descricao.getText());
                if (existente.getQuantidade() == null || existente.getQuantidade() < 1) {
                    existente.setQuantidade(1);
                }
                existente.setPreco(toBigDecimal(preco.getValue()));
                existente.setObservacao(obs.getText());
                existente.setEstoque((Integer) estoque.getValue());
                itemDAO.atualizar(existente);
            }
            carregarCardapioAdm();
        } catch (SQLException ex) { showError(ex); }
    }

    private JPanel tabPedidos() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnAtribuirEntregador = new JButton("Atribuir Entregador...");
        JButton btnMudarStatus = new JButton("Mudar Status...");
        JButton btnCancelar = new JButton("Cancelar Pedido");
        top.add(btnAtualizar); top.add(btnAtribuirEntregador); top.add(btnMudarStatus); top.add(btnCancelar);

        btnAtualizar.addActionListener(e -> carregarPedidosPorStatus());
        btnAtribuirEntregador.addActionListener(e -> atribuirEntregadorDaAbaAtiva());
        btnMudarStatus.addActionListener(e -> mudarStatusPedidoDaAbaAtiva());
        btnCancelar.addActionListener(e -> cancelarPedidoDaAbaAtiva());

        tabsPedidos = new JTabbedPane();
        tabelasPorStatus.clear();
        for (String st : STATUS_TABS) {
            JTable t = new JTable(novoModeloPedidos());
            t.setAutoCreateRowSorter(true);
            tabelasPorStatus.put(st, t);
            JScrollPane sp = new JScrollPane(t);
            String titulo = switch (st) {
                case "pendente"   -> "Pendente";
                case "em_preparo" -> "Em preparo";
                case "enviado"    -> "Enviado";
                case "entregue"   -> "Entregue";
                case "cancelado"  -> "Cancelado";
                default           -> st;
            };
            tabsPedidos.addTab(titulo, sp);
        }

        p.add(top, BorderLayout.NORTH);
        p.add(tabsPedidos, BorderLayout.CENTER);
        return p;
    }

    private ModeloTabelaNaoEditavel novoModeloPedidos() {
        return new ModeloTabelaNaoEditavel(
                new Object[]{"ID","Cliente","Restaurante","Entregador","Status","Total","Data/Hora"}, 0
        );
    }

    private void carregarPedidosPorStatus() {
        try {
            for (JTable t : tabelasPorStatus.values()) {
                ((ModeloTabelaNaoEditavel) t.getModel()).setRowCount(0);
            }

            List<Pedido> lista = pedidoDAO.listarTodos();
            for (Pedido pd : lista) {
                String st = pd.getStatus();
                JTable tabela = tabelasPorStatus.getOrDefault(st, tabelasPorStatus.get("pendente"));
                var m = (ModeloTabelaNaoEditavel) tabela.getModel();

                Restaurante r = restauranteDAO.buscarPorId(pd.getIdRestaurante());
                String nomeRest = (r != null) ? r.getNome() : "-";
                String nomeEnt = "-";
                if (pd.getIdEntregador() != null) {
                    Entregador ent = entregadorDAO.buscarPorId(pd.getIdEntregador());
                    if (ent != null) nomeEnt = ent.getNome();
                }
                m.addRow(new Object[]{
                        pd.getIdPedido(),
                        pd.getIdCliente(),
                        nomeRest,
                        nomeEnt,
                        pd.getStatus(),
                        pd.getValorTotal(),
                        pd.getDataHora()
                });
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private JTable getTabelaPedidosAtiva() {
        int idx = tabsPedidos.getSelectedIndex();
        if (idx < 0 || idx >= STATUS_TABS.length) return tabelasPorStatus.get("pendente");
        return tabelasPorStatus.get(STATUS_TABS[idx]);
    }

    private Integer getIdPedidoSelecionadoNaAbaAtiva() {
        JTable t = getTabelaPedidosAtiva();
        int row = t.getSelectedRow();
        if (row < 0) return null;
        int modelRow = t.convertRowIndexToModel(row);
        Object idObj = t.getModel().getValueAt(modelRow, 0);
        if (idObj instanceof Integer i) return i;
        try { return Integer.parseInt(String.valueOf(idObj)); } catch (Exception e) { return null; }
    }

    private void atribuirEntregadorDaAbaAtiva() {
        Integer idPedido = getIdPedidoSelecionadoNaAbaAtiva();
        if (idPedido == null) { msg("Selecione um pedido na aba atual."); return; }

        try {
            List<Entregador> entregadores = entregadorDAO.listar();
            if (entregadores.isEmpty()) { msg("Não há entregadores cadastrados."); return; }

            Entregador sel = (Entregador) JOptionPane.showInputDialog(
                    this, "Escolha o entregador:", "Atribuir Entregador",
                    JOptionPane.PLAIN_MESSAGE, null, entregadores.toArray(), null
            );
            if (sel == null) return;

            pedidoDAO.atribuirEntregador(idPedido, sel.getIdEntregador());
            entregadorDAO.atualizarStatus(sel.getIdEntregador(), "ocupado");
            carregarPedidosPorStatus();
        } catch (SQLException ex) { showError(ex); }
    }

    private void mudarStatusPedidoDaAbaAtiva() {
        Integer idPedido = getIdPedidoSelecionadoNaAbaAtiva();
        if (idPedido == null) { msg("Selecione um pedido na aba atual."); return; }

        String[] statuses = {"pendente","em_preparo","enviado","entregue","cancelado"};
        String novo = (String) JOptionPane.showInputDialog(this, "Novo status:",
                "Atualizar Pedido", JOptionPane.PLAIN_MESSAGE, null, statuses, statuses[0]);
        if (novo == null) return;

        try {
            pedidoDAO.atualizarStatus(idPedido, novo);
            if (novo.equals("entregue") || novo.equals("cancelado")) {
                Pedido p = pedidoDAO.buscarPorId(idPedido);
                if (p.getIdEntregador() != null) entregadorDAO.atualizarStatus(p.getIdEntregador(), "disponivel");
            }
            carregarPedidosPorStatus();
            for (int i = 0; i < STATUS_TABS.length; i++) {
                if (STATUS_TABS[i].equals(novo)) { tabsPedidos.setSelectedIndex(i); break; }
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void cancelarPedidoDaAbaAtiva() {
        Integer idPedido = getIdPedidoSelecionadoNaAbaAtiva();
        if (idPedido == null) { msg("Selecione um pedido na aba atual."); return; }

        if (JOptionPane.showConfirmDialog(this, "Cancelar o pedido "+idPedido+"?",
                "Confirmar", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        try {
            pedidoDAO.cancelarPedido(idPedido);
            Pedido p = pedidoDAO.buscarPorId(idPedido);
            if (p.getIdEntregador() != null) entregadorDAO.atualizarStatus(p.getIdEntregador(), "disponivel");
            carregarPedidosPorStatus();
            for (int i = 0; i < STATUS_TABS.length; i++) {
                if (STATUS_TABS[i].equals("cancelado")) { tabsPedidos.setSelectedIndex(i); break; }
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private BigDecimal toBigDecimal(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal bd) return bd;
        String s = v.toString().replace(".", "").replace(",", "."); // pt-BR
        try { return new BigDecimal(s); } catch (Exception e) { return BigDecimal.ZERO; }
    }
    private void msg(String s) { JOptionPane.showMessageDialog(this, s); }
    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
