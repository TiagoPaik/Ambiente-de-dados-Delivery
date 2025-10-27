package Delivery.dao;

import Delivery.modelo.ItemPedido;
import Delivery.ui.PedidoDialog;
import util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ItemPedidoDAO {

    // ===== CRUD de itens de cardápio =====

    public List<ItemPedido> listarPorRestaurante(int idRestaurante) throws SQLException {
        String sql = """
            SELECT id_item, id_restaurante, descricao, quantidade, preco, observacao, estoque
            FROM ItemPedido
            WHERE id_restaurante = ?
            ORDER BY descricao
        """;
        List<ItemPedido> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idRestaurante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public ItemPedido buscarPorId(int idItem) throws SQLException {
        String sql = """
            SELECT id_item, id_restaurante, descricao, quantidade, preco, observacao, estoque
            FROM ItemPedido
            WHERE id_item = ?
        """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idItem);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public ItemPedido inserir(ItemPedido it) throws SQLException {
        // ===== Validação antes de inserir =====
        validarCamposObrigatorios(it);

        String sql = """
            INSERT INTO ItemPedido (id_restaurante, descricao, quantidade, preco, observacao, estoque)
            VALUES (?,?,?,?,?,?)
        """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, it.getIdRestaurante());
            ps.setString(2, it.getDescricao());
            ps.setInt(3, it.getQuantidade());
            ps.setBigDecimal(4, it.getPreco());
            ps.setString(5, it.getObservacao());
            ps.setInt(6, it.getEstoque());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) it.setIdItem(rs.getInt(1));
            }
        }
        return it;
    }

    public void atualizar(ItemPedido it) throws SQLException {
        // ===== Validação antes de atualizar =====
        validarCamposObrigatorios(it);

        String sql = """
            UPDATE ItemPedido
               SET descricao=?, quantidade=?, preco=?, observacao=?, estoque=?
             WHERE id_item = ?
        """;
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, it.getDescricao());
            ps.setInt(2, it.getQuantidade());
            ps.setBigDecimal(3, it.getPreco());
            ps.setString(4, it.getObservacao());
            ps.setInt(5, it.getEstoque());
            ps.setInt(6, it.getIdItem());
            ps.executeUpdate();
        }
    }

    public void excluir(int idItem) throws SQLException {
        String sql = "DELETE FROM ItemPedido WHERE id_item = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idItem);
            ps.executeUpdate();
        }
    }

    // ===== Reposição de estoque (ADM) =====
    public void reporEstoque(int idItem, int quantidade) throws SQLException {
        String sql = "UPDATE ItemPedido SET estoque = estoque + ? WHERE id_item = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Math.max(0, quantidade));
            ps.setInt(2, idItem);
            ps.executeUpdate();
        }
    }

    // ===== Verificação & baixa para a venda =====
    public String verificarDisponibilidade(List<PedidoDialog.LinhaPedido> linhas) throws SQLException {
        if (linhas == null || linhas.isEmpty()) return "Carrinho vazio";
        String inSql = String.join(",", Collections.nCopies(linhas.size(), "?"));
        String sql = "SELECT id_item, descricao, estoque FROM ItemPedido WHERE id_item IN (" + inSql + ")";

        Map<Integer, Integer> est = new HashMap<>();
        Map<Integer, String> descs = new HashMap<>();

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            for (var lp : linhas) ps.setInt(i++, lp.idItem);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    est.put(rs.getInt("id_item"), rs.getInt("estoque"));
                    descs.put(rs.getInt("id_item"), rs.getString("descricao"));
                }
            }
        }

        StringBuilder faltas = new StringBuilder();
        for (var lp : linhas) {
            int disponivel = est.getOrDefault(lp.idItem, 0);
            if (disponivel < lp.qtd) {
                String nome = descs.getOrDefault(lp.idItem, lp.descricao);
                faltas.append(String.format("- %s: solicitado %d, disponível %d%n", nome, lp.qtd, disponivel));
            }
        }
        return faltas.length() > 0 ? faltas.toString() : null;
    }

    public boolean baixarEstoqueEmLote(Connection c, List<PedidoDialog.LinhaPedido> linhas) throws SQLException {
        String sql = "UPDATE ItemPedido SET estoque = estoque - ? WHERE id_item = ? AND estoque >= ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (var lp : linhas) {
                ps.setInt(1, lp.qtd);
                ps.setInt(2, lp.idItem);
                ps.setInt(3, lp.qtd);
                int ok = ps.executeUpdate();
                if (ok == 0) return false; // sem estoque suficiente
            }
            return true;
        }
    }

    // ===== Validação de campos obrigatórios =====
    private void validarCamposObrigatorios(ItemPedido it) throws SQLException {
        if (it.getDescricao() == null || it.getDescricao().trim().isEmpty()) {
            throw new SQLException("O campo Descrição não pode ficar em branco.");
        }
        if (it.getQuantidade() == null || it.getQuantidade() <= 0) {
            throw new SQLException("O campo Quantidade deve ser maior que zero.");
        }
        if (it.getPreco() == null || it.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new SQLException("O campo Preço deve ser maior que zero.");
        }
        if (it.getEstoque() == null || it.getEstoque() < 0) {
            throw new SQLException("O campo Estoque não pode ser negativo ou vazio.");
        }
    }

    // ===== Mapper =====
    private ItemPedido map(ResultSet rs) throws SQLException {
        ItemPedido it = new ItemPedido();
        it.setIdItem(rs.getInt("id_item"));
        it.setIdRestaurante(rs.getInt("id_restaurante"));
        it.setDescricao(rs.getString("descricao"));
        it.setQuantidade(rs.getInt("quantidade"));
        it.setPreco(rs.getBigDecimal("preco"));
        it.setObservacao(rs.getString("observacao"));
        it.setEstoque(rs.getInt("estoque"));
        return it;
    }
}
