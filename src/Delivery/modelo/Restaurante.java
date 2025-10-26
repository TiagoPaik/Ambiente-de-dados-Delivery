package Delivery.modelo;

public class Restaurante {
    private Integer idRestaurante;
    private String nome;
    private String tipoCozinha;
    private String telefone;
    private String endereco;

    public Restaurante() {}

    // Construtor usado no seu código: new Restaurante(null, nome, tipo, tel, end)
    public Restaurante(Integer idRestaurante, String nome, String tipoCozinha, String telefone, String endereco) {
        this.idRestaurante = idRestaurante;
        this.nome = nome;
        this.tipoCozinha = tipoCozinha;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public Integer getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Integer idRestaurante) { this.idRestaurante = idRestaurante; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipoCozinha() { return tipoCozinha; }
    public void setTipoCozinha(String tipoCozinha) { this.tipoCozinha = tipoCozinha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
}
