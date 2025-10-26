package Delivery.ui;

import Delivery.dao.ClienteDAO;
import Delivery.modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CadastroClienteFrame extends JDialog {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public CadastroClienteFrame(Frame owner) {
        super(owner, "Cadastrar Cliente", true);
        setSize(420, 420);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.LINE_END;

        JTextField nome = new JTextField(22);
        JTextField tel = new JTextField(22);
        JTextField end = new JTextField(22);
        JTextField email = new JTextField(22);
        JPasswordField senha = new JPasswordField(22);
        JTextField cpf = new JTextField(22);

        int y=0;
        c.gridx=0;c.gridy=y; add(new JLabel("Nome:"), c);     c.gridx=1;c.anchor=GridBagConstraints.LINE_START; add(nome, c); y++;
        c.gridx=0;c.gridy=y;c.anchor=GridBagConstraints.LINE_END; add(new JLabel("Telefone:"), c); c.gridx=1;c.anchor=GridBagConstraints.LINE_START; add(tel,c); y++;
        c.gridx=0;c.gridy=y;c.anchor=GridBagConstraints.LINE_END; add(new JLabel("Endereço:"), c); c.gridx=1;c.anchor=GridBagConstraints.LINE_START; add(end,c); y++;
        c.gridx=0;c.gridy=y;c.anchor=GridBagConstraints.LINE_END; add(new JLabel("Email:"), c); c.gridx=1;c.anchor=GridBagConstraints.LINE_START; add(email,c); y++;
        c.gridx=0;c.gridy=y;c.anchor=GridBagConstraints.LINE_END; add(new JLabel("Senha:"), c); c.gridx=1;c.anchor=GridBagConstraints.LINE_START; add(senha,c); y++;
        c.gridx=0;c.gridy=y;c.anchor=GridBagConstraints.LINE_END; add(new JLabel("CPF:"), c); c.gridx=1;c.anchor=GridBagConstraints.LINE_START; add(cpf,c); y++;

        JButton salvar = new JButton("Salvar");
        JButton cancelar = new JButton("Cancelar");
        JPanel botoes = new JPanel(); botoes.add(salvar); botoes.add(cancelar);
        c.gridx=0;c.gridy=y;c.gridwidth=2;c.anchor=GridBagConstraints.CENTER; add(botoes, c);

        salvar.addActionListener(evt -> {
            try {
                Cliente cli = new Cliente();
                cli.setNome(nome.getText().trim());
                cli.setTelefone(tel.getText().trim());
                cli.setEndereco(end.getText().trim());
                cli.setEmail(email.getText().trim());
                cli.setSenha(new String(senha.getPassword()));
                cli.setCpf(cpf.getText().trim());

                clienteDAO.inserir(cli);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado! ID: " + cli.getIdCliente());
                dispose();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelar.addActionListener(e -> dispose());
    }
}
