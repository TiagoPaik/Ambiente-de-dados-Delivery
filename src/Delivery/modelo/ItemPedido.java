package Delivery.modelo;

import java.math.BigDecimal;

public class ItemPedido {
    private Integer idItem;
    private Integer idRestaurante;
    private String descricao;
    private Integer quantidade; // porção padrão (ex.: 1)
    private BigDecimal preco;
    private String observacao;
    private Integer estoque;    // NOVO: controle de estoque

    public Integer getIdItem() { return idItem; }
    public void setIdItem(Integer idItem) { this.idItem = idItem; }

    public Integer getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Integer idRestaurante) { this.idRestaurante = idRestaurante; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    @Override
    public String toString() {
        return descricao + " - R$ " + preco + " (estoque: " + (estoque == null ? 0 : estoque) + ")";
    }
}
