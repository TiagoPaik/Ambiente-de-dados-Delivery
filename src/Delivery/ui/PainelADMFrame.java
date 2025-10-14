package Delivery.ui;

import Delivery.dao.EntregadorDAO;
import Delivery.dao.PedidoDAO;
import Delivery.dao.RestauranteDAO;
import Delivery.modelo.Entregador;
import Delivery.modelo.Restaurante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class PainelADMFrame extends JFrame {

    private final RestauranteDAO restauranteDAO = new RestauranteDAO();
    private final EntregadorDAO entregadorDAO = new EntregadorDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final JTable tabelaPedidos = new JTable(
            new DefaultTableModel(new Object[]{"ID","Cliente","Rest.","Status","Total","Data/Hora"},0)
    );

    public PainelADMFrame() {
        super("Painel do Administrador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // top: logout
        JButton btnLogout = new JButton("Sair / Logout");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(new JLabel("Administrador"));
        top.add(btnLogout);
        add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Restaurantes", tabRestaurante());
        tabs.addTab("Entregadores", tabEntregador());
        tabs.addTab("Pedidos", tabPedidos());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel tabRestaurante() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(6,6,6,6);

        JTextField nome = new JTextField(18);
        JTextField tipo = new JTextField(12);
        JTextField tel  = new JTextField(12);
        JTextField end  = new JTextField(18);
        JButton salvar  = new JButton("Cadastrar");

        int y=0;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Nome:"), c);
        c.gridx=1; p.add(nome, c); y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Tipo Cozinha:"), c);
        c.gridx=1; p.add(tipo, c); y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Telefone:"), c);
        c.gridx=1; p.add(tel, c); y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Endereço:"), c);
        c.gridx=1; p.add(end, c); y++;
        c.gridx=0; c.gridy=y; c.gridwidth=2; c.anchor=GridBagConstraints.CENTER;
        p.add(salvar, c);

        salvar.addActionListener(e -> {
            try {
                Restaurante r = restauranteDAO.inserir(new Restaurante(null, nome.getText(), tipo.getText(), tel.getText(), end.getText()));
                JOptionPane.showMessageDialog(this, "Restaurante cadastrado. ID: " + r.getIdRestaurante());
                nome.setText(""); tipo.setText(""); tel.setText(""); end.setText("");
            } catch (SQLException ex) { showError(ex); }
        });

        return p;
    }

    private JPanel tabEntregador() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(6,6,6,6);

        JTextField nome = new JTextField(18);
        JTextField veic = new JTextField(12);
        JButton salvar = new JButton("Cadastrar Entregador");

        int y=0;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Nome:"), c);
        c.gridx=1; p.add(nome, c); y++;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Veículo:"), c);
        c.gridx=1; p.add(veic, c); y++;
        c.gridx=0; c.gridy=y; c.gridwidth=2; c.anchor=GridBagConstraints.CENTER;
        p.add(salvar, c);

        salvar.addActionListener(e -> {
            try {
                Entregador ent = entregadorDAO.inserir(new Entregador(null, nome.getText(), "disponivel", veic.getText()));
                JOptionPane.showMessageDialog(this, "Entregador criado. ID: " + ent.getIdEntregador());
                nome.setText(""); veic.setText("");
            } catch (SQLException ex) { showError(ex); }
        });
        return p;
    }

    private JPanel tabPedidos() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField idCliente = new JTextField(8);
        JButton listar = new JButton("Listar por Cliente");
        JButton atualizar = new JButton("Atualizar Status...");
        top.add(new JLabel("ID Cliente:")); top.add(idCliente); top.add(listar); top.add(atualizar);

        listar.addActionListener(e -> {
            try {
                DefaultTableModel m = (DefaultTableModel) tabelaPedidos.getModel();
                m.setRowCount(0);
                var lista = pedidoDAO.listarPorCliente(Integer.parseInt(idCliente.getText()));
                for (var pd : lista) {
                    m.addRow(new Object[]{pd.getIdPedido(), pd.getIdCliente(), pd.getIdRestaurante(),
                            pd.getStatus(), pd.getValorTotal(), pd.getDataHora()});
                }
            } catch (Exception ex) { showError(ex); }
        });

        atualizar.addActionListener(e -> abrirDialogAtualizarStatus());

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(tabelaPedidos), BorderLayout.CENTER);
        return p;
    }

    private void abrirDialogAtualizarStatus() {
        int row = tabelaPedidos.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um pedido."); return; }
        int id = (int) tabelaPedidos.getValueAt(row, 0);
        String[] statuses = {"pendente","em_preparo","enviado","entregue","cancelado"};
        String novo = (String) JOptionPane.showInputDialog(this,"Novo status:",
                "Atualizar Pedido", JOptionPane.PLAIN_MESSAGE, null, statuses, statuses[0]);
        if (novo != null) {
            try {
                pedidoDAO.atualizarStatus(id, novo);
                ((DefaultTableModel) tabelaPedidos.getModel()).setValueAt(novo, row, 3);
            } catch (SQLException e) { showError(e); }
        }
    }

    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
