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

        int y = 0;
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
                // Validação de campos vazios
                if (nome.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O campo Nome não pode ficar em branco", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (tel.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O campo Telefone não pode ficar em branco", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (end.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O campo Endereço não pode ficar em branco", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (email.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O campo Email não pode ficar em branco", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Validação do formato do e-mail
                String emailText = email.getText().trim();
                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                if (!emailText.matches(emailRegex)) {
                    JOptionPane.showMessageDialog(this, "O campo Email deve estar no formato email@example.com", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Validação da senha
                String senhaText = new String(senha.getPassword());
                if (senhaText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "O campo Senha não pode ficar em branco", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String senhaRegex = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";
                if (!senhaText.matches(senhaRegex)) {
                    JOptionPane.showMessageDialog(this,
                            "A senha deve conter no mínimo 8 caracteres, com pelo menos 1 letra maiúscula e 1 letra minúscula.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Validação de CPF
                String cpfText = cpf.getText().trim().replaceAll("[^0-9]", ""); // remove pontos e traços
                if (!isValidCPF(cpfText)) {
                    JOptionPane.showMessageDialog(this,
                            "O campo CPF deve conter um número válido com 11 dígitos.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Se todos os campos estiverem válidos:
                Cliente cli = new Cliente();
                cli.setNome(nome.getText().trim());
                cli.setTelefone(tel.getText().trim());
                cli.setEndereco(end.getText().trim());
                cli.setEmail(emailText);
                cli.setSenha(senhaText);
                cli.setCpf(cpfText);

                clienteDAO.inserir(cli);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado! ID: " + cli.getIdCliente());
                dispose();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro inesperado:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelar.addActionListener(e -> dispose());
    }

    /**
     * Verifica se um CPF é válido.
     */
    private boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma1 = 0, soma2 = 0;
            for (int i = 0; i < 9; i++) {
                int num = cpf.charAt(i) - '0';
                soma1 += num * (10 - i);
                soma2 += num * (11 - i);
            }

            int digito1 = (soma1 * 10) % 11;
            if (digito1 == 10) digito1 = 0;

            soma2 += digito1 * 2;
            int digito2 = (soma2 * 10) % 11;
            if (digito2 == 10) digito2 = 0;

            return digito1 == (cpf.charAt(9) - '0') && digito2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }
}
