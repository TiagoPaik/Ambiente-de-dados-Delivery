package Delivery.modelo;

public class Admin extends Usuario {
    private Integer idAdmin;

    public Admin() {}
    public Admin(Integer idAdmin, String email, String senha) {
        super(email, senha);
        this.idAdmin = idAdmin;
    }
    public Integer getIdAdmin() { return idAdmin; }
    public void setIdAdmin(Integer idAdmin) { this.idAdmin = idAdmin; }
}
