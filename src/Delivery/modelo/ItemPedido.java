package Delivery.modelo;

import java.math.BigDecimal;

public class ItemPedido {
    private Integer idItem;
    private Integer idPedido;
    private String descricao;
    private Integer quantidade;
    private BigDecimal preco;
    private String observacao;

    public ItemPedido() {}
    public ItemPedido(Integer idItem, Integer idPedido, String descricao, Integer quantidade,
                      BigDecimal preco, String observacao) {
        this.idItem = idItem;
        this.idPedido = idPedido;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.preco = preco;
        this.observacao = observacao;
    }

    public Integer getIdItem() { return idItem; }
    public void setIdItem(Integer idItem) { this.idItem = idItem; }
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
