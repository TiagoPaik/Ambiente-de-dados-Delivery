package Delivery.ui;

import Delivery.dao.EntregadorDAO;
import Delivery.dao.ItemPedidoDAO;
import Delivery.dao.PedidoDAO;
import Delivery.dao.RestauranteDAO;
import Delivery.modelo.Cliente;
import Delivery.modelo.Pedido;
import Delivery.modelo.Restaurante;
import Delivery.ui.tabelas.ModeloTabelaNaoEditavel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PainelClienteFrame extends JFrame {
    private final Cliente cliente;
    private final RestauranteDAO restauranteDAO = new RestauranteDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ItemPedidoDAO itemDAO = new ItemPedidoDAO();
    private final EntregadorDAO entregadorDAO = new EntregadorDAO();

    private final JTable tabelaRest = new JTable();
    private final JTable tabelaPedidos = new JTable();

    // Mantém o mapeamento da linha -> objeto Restaurante (para pegar o ID sem exibir)
    private List<Restaurante> restaurantesCarregados = new ArrayList<>();

    public PainelClienteFrame(Cliente cliente) {
        super("Cliente - " + cliente.getNome());
        this.cliente = cliente;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // top: logout
        JButton btnLogout = new JButton("Sair / Logout");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(new JLabel("Logado como: " + cliente.getNome()));
        top.add(btnLogout);
        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Restaurantes", montarTabRestaurantes());
        tabs.addTab("Meus Pedidos", montarTabPedidos());

        add(tabs, BorderLayout.CENTER);

        carregarRestaurantes();
        carregarPedidos();
    }

    private JPanel montarTabRestaurantes() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        // Sem coluna de ID visível
        tabelaRest.setModel(new ModeloTabelaNaoEditavel(
                new Object[]{"Nome","Tipo","Telefone","Endereço"}, 0
        ));
        p.add(new JScrollPane(tabelaRest), BorderLayout.CENTER);

        JButton novoPedido = new JButton("Fazer Pedido...");
        novoPedido.addActionListener(e -> abrirDialogPedido());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(novoPedido);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private JPanel montarTabPedidos() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        // Sem IDs: mostra nome do restaurante
        tabelaPedidos.setModel(new ModeloTabelaNaoEditavel(
                new Object[]{"Restaurante","Status","Total","Data/Hora"}, 0
        ));
        p.add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);

        JButton atualizar = new JButton("Atualizar");
        atualizar.addActionListener(e -> carregarPedidos());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(atualizar);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void carregarRestaurantes() {
        try {
            restaurantesCarregados = restauranteDAO.listar(); // guarda para mapear seleção -> id
            var m = (ModeloTabelaNaoEditavel) tabelaRest.getModel();
            m.setRowCount(0);
            for (Restaurante r : restaurantesCarregados) {
                m.addRow(new Object[]{ r.getNome(), r.getTipoCozinha(), r.getTelefone(), r.getEndereco() });
            }
        } catch (SQLException e) { showError(e); }
    }

    private void carregarPedidos() {
        try {
            var m = (ModeloTabelaNaoEditavel) tabelaPedidos.getModel();
            m.setRowCount(0);
            List<Pedido> lista = pedidoDAO.listarPorCliente(cliente.getIdCliente());
            for (Pedido p : lista) {
                // Buscar o nome do restaurante correspondente
                Restaurante rest = restauranteDAO.buscarPorId(p.getIdRestaurante());
                String nomeRest = (rest != null) ? rest.getNome() : "(Desconhecido)";

                // Adiciona linha sem IDs
                m.addRow(new Object[]{ nomeRest, p.getStatus(), p.getValorTotal(), p.getDataHora() });
            }
        } catch (SQLException e) { showError(e); }
    }

    private void abrirDialogPedido() {
        int row = tabelaRest.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um restaurante."); return; }

        // Recupera o ID através da lista mapeada (sem exibir ID na tabela)
        int idRest = restaurantesCarregados.get(row).getIdRestaurante();

        new PedidoDialog(
                this,
                cliente.getIdCliente(),
                idRest,
                pedidoDAO,
                itemDAO,
                this::carregarPedidos // callback: atualiza a aba "Meus Pedidos" ao finalizar
        ).setVisible(true);
    }

    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
