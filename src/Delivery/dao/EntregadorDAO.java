package Delivery.dao;

import Delivery.config.Conexao;
import Delivery.modelo.Entregador;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregadorDAO {

    public Entregador inserir(Entregador e) throws SQLException {
        try {
            validarEntregador(e); // faz a verificação antes de inserir

            String sql = "INSERT INTO Entregador (nome, status, veiculo) VALUES (?,?,?)";

            try (Connection con = Conexao.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, e.getNome());
                ps.setString(2, e.getStatus());
                ps.setString(3, e.getVeiculo());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) e.setIdEntregador(rs.getInt(1));
                }

                JOptionPane.showMessageDialog(null, "Entregador cadastrado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                return e;

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao inserir entregador no banco de dados:\n" + ex.getMessage(),
                        "Erro de Banco de Dados",
                        JOptionPane.ERROR_MESSAGE);
                throw ex;
            }

        } catch (IllegalArgumentException ex) {
            // CORREÇÃO:
            // Exibe o pop-up de alerta amarelo para falha de validação.
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Validação de Dados",
                    JOptionPane.WARNING_MESSAGE);
            // Retorna null para encerrar a execução do método aqui.
            // Isso evita lançar uma nova exceção que seria capturada
            // pelo bloco 'catch (Exception ex)' ou pelo código chamador,
            // que é o que estava gerando o segundo pop-up de erro (vermelho).
            return null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Erro inesperado: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            throw new SQLException("Erro inesperado ao inserir entregador.", ex);
        }
    }

    public List<Entregador> listarDisponiveis() throws SQLException {
        String sql = "SELECT * FROM Entregador WHERE status='disponivel'";
        List<Entregador> lista = new ArrayList<>();

        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Entregador(
                        rs.getInt("id_entregador"),
                        rs.getString("nome"),
                        rs.getString("status"),
                        rs.getString("veiculo")
                ));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao listar entregadores disponíveis:\n" + ex.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
            throw ex;
        }

        return lista;
    }

    /**
     * Valida os campos obrigatórios do entregador antes de inserir no banco.
     */
    private void validarEntregador(Entregador e) {
        if (e == null) {
            throw new IllegalArgumentException("O objeto Entregador não pode ser nulo.");
        }

        if (e.getNome() == null || e.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do entregador não pode ficar em branco.");
        }

        if (e.getVeiculo() == null || e.getVeiculo().trim().isEmpty()) {
            throw new IllegalArgumentException("O campo veículo não pode ficar em branco.");
        }

        if (e.getStatus() == null || e.getStatus().trim().isEmpty()) {
            // Define status padrão se não for informado
            e.setStatus("disponivel");
        }
    }
}