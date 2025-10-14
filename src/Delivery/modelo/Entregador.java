package Delivery.modelo;

public class Entregador {
    private Integer idEntregador;
    private String nome;
    private String status; // 'disponivel', 'ocupado', 'inativo'
    private String veiculo;

    public Entregador() {}
    public Entregador(Integer id, String nome, String status, String veiculo) {
        this.idEntregador = id;
        this.nome = nome;
        this.status = status;
        this.veiculo = veiculo;
    }

    public Integer getIdEntregador() { return idEntregador; }
    public void setIdEntregador(Integer idEntregador) { this.idEntregador = idEntregador; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }
}
