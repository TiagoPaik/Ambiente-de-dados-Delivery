package Delivery.modelo;

public abstract class Usuario {
    protected String email;
    protected String senha;

    public Usuario() {}
    public Usuario(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
