package Delivery.ui;

import Delivery.dao.ClienteDAO;
import Delivery.modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

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
                validarCampos(tel.getText(), end.getText(), email.getText(), new String(senha.getPassword()), cpf.getText());

                Cliente cli = new Cliente(
                        null,
                        nome.getText().trim(),
                        tel.getText().trim(),
                        end.getText().trim(),
                        email.getText().trim(),
                        new String(senha.getPassword()),
                        cpf.getText().trim()
                );

                cli = clienteDAO.inserir(cli);
                JOptionPane.showMessageDialog(this, "Cadastrado com sucesso!\nID: " + cli.getIdCliente());
                dispose();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validação", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro no Banco", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(p);
    }

    private void validarCampos(String telefone, String endereco, String email, String senha, String cpf) {
        // Telefone
        if (telefone == null || telefone.trim().isEmpty())
            throw new IllegalArgumentException("O telefone não pode ser vazio.");
        if (telefone.replaceAll("\\D", "").length() < 11)
            throw new IllegalArgumentException("O telefone deve conter pelo menos 11 dígitos (DDD + número).");

        // Endereço
        if (endereco == null || endereco.trim().isEmpty())
            throw new IllegalArgumentException("O endereço não pode ser vazio.");

        // Email
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("O e-mail não pode ser vazio.");
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!Pattern.matches(emailRegex, email))
            throw new IllegalArgumentException("E-mail inválido. Use o formato: exemplo@dominio.com");

        // Senha
        if (senha == null || senha.trim().isEmpty())
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        if (senha.length() < 8)
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        if (!senha.matches(".*[A-Z].*") || !senha.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("""
                    A senha deve conter:
                    • Pelo menos uma letra maiúscula
                    • Pelo menos uma letra minúscula
                    • Mínimo de 8 caracteres
                    """);
        }

        // CPF
        if (!isCPFValido(cpf))
            throw new IllegalArgumentException("CPF inválido.");
    }

    private boolean isCPFValido(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0, resto;
            for (int i = 1; i <= 9; i++)
                soma += Integer.parseInt(cpf.substring(i - 1, i)) * (11 - i);
            resto = (soma * 10) % 11;
            if ((resto == 10) || (resto == 11)) resto = 0;
            if (resto != Integer.parseInt(cpf.substring(9, 10))) return false;

            soma = 0;
            for (int i = 1; i <= 10; i++)
                soma += Integer.parseInt(cpf.substring(i - 1, i)) * (12 - i);
            resto = (soma * 10) % 11;
            if ((resto == 10) || (resto == 11)) resto = 0;

            return resto == Integer.parseInt(cpf.substring(10, 11));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
