package Delivery.ui;

import Delivery.dao.AdminDAO;
import Delivery.dao.ClienteDAO;
import Delivery.modelo.Admin;
import Delivery.modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final AdminDAO adminDAO = new AdminDAO();

    public LoginFrame() {
        super("Sistema de Delivery - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        // === Aba Cliente ===
        JPanel clientePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.LINE_END;
        clientePanel.add(new JLabel("E-mail:"), c);
        c.gridy++;
        clientePanel.add(new JLabel("Senha:"), c);

        JTextField emailC = new JTextField(22);
        JPasswordField senhaC = new JPasswordField(22);
        c.gridx = 1; c.gridy = 0; c.anchor = GridBagConstraints.LINE_START;
        clientePanel.add(emailC, c);
        c.gridy++;
        clientePanel.add(senhaC, c);

        JButton entrarC = new JButton("Entrar");
        JButton cadastrarC = new JButton("Cadastrar...");
        JPanel botoesC = new JPanel(); botoesC.add(entrarC); botoesC.add(cadastrarC);
        c.gridx = 0; c.gridy++; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        clientePanel.add(botoesC, c);

        entrarC.addActionListener(evt -> {
            String email = emailC.getText().trim();
            String senha = new String(senhaC.getPassword());

            // === BACKDOOR CLIENTE (APENAS PARA TESTE LOCAL) ===
            if (email.equalsIgnoreCase("test") && senha.equals("test")) {
                Cliente fake = new Cliente(9999, "Cliente Demo", "85999999999",
                        "Rua Demo, 123", "cliente@demo.com", "NAO_USAR", "000.000.000-00");
                SwingUtilities.invokeLater(() -> new PainelClienteFrame(fake).setVisible(true));
                dispose();
                return;
            }
            // === FIM BACKDOOR ===

            try {
                Cliente cli = clienteDAO.buscarPorEmailSenha(email, senha);
                if (cli != null) {
                    SwingUtilities.invokeLater(() -> new PainelClienteFrame(cli).setVisible(true));
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "E-mail/senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                showError(e);
            }
        });

        cadastrarC.addActionListener(e -> SwingUtilities.invokeLater(() -> new CadastroClienteFrame(this).setVisible(true)));

        // === Aba Admin ===
        JPanel adminPanel = new JPanel(new GridBagLayout());
        GridBagConstraints a = new GridBagConstraints();
        a.insets = new Insets(6,6,6,6);
        a.gridx = 0; a.gridy = 0; a.anchor = GridBagConstraints.LINE_END;
        adminPanel.add(new JLabel("E-mail:"), a);
        a.gridy++;
        adminPanel.add(new JLabel("Senha:"), a);

        JTextField emailA = new JTextField(22);
        JPasswordField senhaA = new JPasswordField(22);
        a.gridx = 1; a.gridy = 0; a.anchor = GridBagConstraints.LINE_START;
        adminPanel.add(emailA, a);
        a.gridy++;
        adminPanel.add(senhaA, a);

        JButton entrarA = new JButton("Entrar como Admin");
        a.gridx = 0; a.gridy++; a.gridwidth = 2; a.anchor = GridBagConstraints.CENTER;
        adminPanel.add(entrarA, a);

        entrarA.addActionListener(evt -> {
            String email = emailA.getText().trim();
            String senha = new String(senhaA.getPassword());

            // === BACKDOOR ADMIN (APENAS PARA TESTE LOCAL) ===
            if (email.equalsIgnoreCase("admin") && senha.equals("admin")) {
                SwingUtilities.invokeLater(() -> new PainelADMFrame().setVisible(true));
                dispose();
                return;
            }
            // === FIM BACKDOOR ===

            try {
                Admin adm = adminDAO.login(email, senha);
                if (adm != null) {
                    SwingUtilities.invokeLater(() -> new PainelADMFrame().setVisible(true));
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Admin inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                showError(e);
            }
        });

        abas.addTab("Cliente", clientePanel);
        abas.addTab("Administrador", adminPanel);
        setContentPane(abas);
    }

    private void showError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
