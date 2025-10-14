package Delivery.modelo;

import java.time.LocalDateTime;

public class Pedido {
    private Integer idPedido;
    private Integer idCliente;
    private Integer idRestaurante;
    private Integer idEntregador; // pode ser null
    private LocalDateTime dataHora;
    private String status; // pendente, em_preparo, enviado, entregue, cancelado
    private java.math.BigDecimal valorTotal;

    public Pedido() {}

    public Pedido(Integer idPedido, Integer idCliente, Integer idRestaurante, Integer idEntregador,
                  LocalDateTime dataHora, String status, java.math.BigDecimal valorTotal) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.idRestaurante = idRestaurante;
        this.idEntregador = idEntregador;
        this.dataHora = dataHora;
        this.status = status;
        this.valorTotal = valorTotal;
    }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public Integer getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Integer idRestaurante) { this.idRestaurante = idRestaurante; }
    public Integer getIdEntregador() { return idEntregador; }
    public void setIdEntregador(Integer idEntregador) { this.idEntregador = idEntregador; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.math.BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(java.math.BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
