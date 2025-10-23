package Delivery.dao;

import Delivery.config.Conexao;
import Delivery.modelo.Restaurante;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestauranteDAO {

    public Restaurante inserir(Restaurante r) throws SQLException {
        try {
            // 1. Valida os campos antes de tentar inserir
            validarRestaurante(r);

            String sql = "INSERT INTO Restaurante (nome, tipo_cozinha, telefone, endereco) VALUES (?,?,?,?)";

            try (Connection con = Conexao.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, r.getNome());
                ps.setString(2, r.getTipoCozinha());
                ps.setString(3, r.getTelefone());
                ps.setString(4, r.getEndereco());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) r.setIdRestaurante(rs.getInt(1));
                }

                // Exibe mensagem de sucesso após a inserção
                JOptionPane.showMessageDialog(null, "Restaurante cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                return r;

            } catch (SQLException ex) {
                // Captura erros específicos do banco de dados
                JOptionPane.showMessageDialog(null,
                        "Erro ao inserir restaurante no banco de dados:\n" + ex.getMessage(),
                        "Erro de Banco de Dados",
                        JOptionPane.ERROR_MESSAGE);
                throw ex;
            }

        } catch (IllegalArgumentException ex) {
            // Captura erros de validação (campos nulos/vazios)
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Validação de Dados",
                    JOptionPane.WARNING_MESSAGE);
            // Retorna null para parar a execução e evitar o segundo pop-up
            return null;
        } catch (Exception ex) {
            // Captura qualquer outro erro inesperado
            JOptionPane.showMessageDialog(null,
                    "Erro inesperado: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            throw new SQLException("Erro inesperado ao inserir restaurante.", ex);
        }
    }

    public List<Restaurante> listar() throws SQLException {
        String sql = "SELECT * FROM Restaurante";
        List<Restaurante> lista = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Restaurante(
                        rs.getInt("id_restaurante"),
                        rs.getString("nome"),
                        rs.getString("tipo_cozinha"),
                        rs.getString("telefone"),
                        rs.getString("endereco")
                ));
            }
        } catch (SQLException ex) {
            // Captura erros específicos do banco de dados na listagem
            JOptionPane.showMessageDialog(null,
                    "Erro ao listar restaurantes:\n" + ex.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
            throw ex;
        }

        return lista;
    }

    /**
     * Valida os campos obrigatórios do restaurante.
     */
    private void validarRestaurante(Restaurante r) {
        if (r == null) {
            throw new IllegalArgumentException("O objeto Restaurante não pode ser nulo.");
        }

        if (r.getNome() == null || r.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo Nome não pode ficar em branco.");
        }

        if (r.getTipoCozinha() == null || r.getTipoCozinha().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo Tipo de Cozinha não pode ficar em branco.");
        }

        if (r.getTelefone() == null || r.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo Telefone não pode ficar em branco.");
        }

        if (r.getEndereco() == null || r.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo Endereço não pode ficar em branco.");
        }
    }
}