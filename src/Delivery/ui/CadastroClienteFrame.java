package Delivery.ui;

import Delivery.dao.ClienteDAO;
import Delivery.modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CadastroClienteFrame extends JFrame {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public CadastroClienteFrame(JFrame parent) {
        super("Cadastro de Cliente");
        setSize(420, 420);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.LINE_END;

        JTextField nome     = new JTextField(22);
        JTextField tel      = new JTextField(22);
        JTextField end      = new JTextField(22);
        JTextField email    = new JTextField(22);
        JPasswordField senha= new JPasswordField(22);
        JTextField cpf      = new JTextField(22);

        int y=0;
        c.gridx=0; c.gridy=y; p.add(new JLabel("Nome:"), c);
        c.gridx=1; c.anchor=GridBagConstraints.LINE_START; p.add(nome, c); y++;

        c.gridx=0; c.gridy=y; c.anchor=GridBagConstraints.LINE_END; p.add(new JLabel("Telefone:"), c);
        c.gridx=1; c.anchor=GridBagConstraints.LINE_START; p.add(tel, c); y++;

        c.gridx=0; c.gridy=y; c.anchor=GridBagConstraints.LINE_END; p.add(new JLabel("Endereço:"), c);
        c.gridx=1; c.anchor=GridBagConstraints.LINE_START; p.add(end, c); y++;

        c.gridx=0; c.gridy=y; c.anchor=GridBagConstraints.LINE_END; p.add(new JLabel("E-mail:"), c);
        c.gridx=1; c.anchor=GridBagConstraints.LINE_START; p.add(email, c); y++;

        c.gridx=0; c.gridy=y; c.anchor=GridBagConstraints.LINE_END; p.add(new JLabel("Senha:"), c);
        c.gridx=1; c.anchor=GridBagConstraints.LINE_START; p.add(senha, c); y++;

        c.gridx=0; c.gridy=y; c.anchor=GridBagConstraints.LINE_END; p.add(new JLabel("CPF:"), c);
        c.gridx=1; c.anchor=GridBagConstraints.LINE_START; p.add(cpf, c); y++;

        JButton salvar = new JButton("Salvar");
        c.gridx=0; c.gridy=++y; c.gridwidth=2; c.anchor=GridBagConstraints.CENTER;
        p.add(salvar, c);

        salvar.addActionListener(evt -> {
            try {
                Cliente cli = new Cliente(null, nome.getText(), tel.getText(), end.getText(),
                        email.getText(), new String(senha.getPassword()), cpf.getText());
                cli = clienteDAO.inserir(cli);
                JOptionPane.showMessageDialog(this, "Cadastrado! ID: " + cli.getIdCliente());
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(p);
    }
}
