package Delivery.modelo;

public class Cliente extends Usuario {
    private Integer idCliente;
    private String nome;
    private String telefone;
    private String endereco;
    private String cpf;

    public Cliente() {}
    public Cliente(Integer idCliente, String nome, String telefone, String endereco,
                   String email, String senha, String cpf) {
        super(email, senha);
        this.idCliente = idCliente;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.cpf = cpf;
    }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
