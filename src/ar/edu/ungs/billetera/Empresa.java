package ar.edu.ungs.billetera;

public class Empresa {
    private String cuit;
    private String razonSocial;

    public Empresa(String cuit, String razonSocial) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }
}