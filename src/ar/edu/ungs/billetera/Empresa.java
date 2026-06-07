package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.List;

public class Empresa {
    private String cuit;
    private String razonSocial;
    private String telefono;
    private String email;
    private String nombreContacto;
    private List<String> autorizados;

    public Empresa(String cuit, String razonSocial, String telefono, String email, String nombreContacto) {
        if (cuit == null || cuit.trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIT es obligatorio.");
        }
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new IllegalArgumentException("La razón social es obligatoria.");
        }
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.email = email;
        this.nombreContacto = nombreContacto;
        this.autorizados = new ArrayList<>();
    }

    public String getCuit() {
        return cuit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void agregarAutorizado(String dni) {
        if (autorizados.contains(dni)) {
            throw new IllegalArgumentException("La persona con DNI " + dni + " ya está autorizada.");
        }
        autorizados.add(dni);
    }

    public boolean estaAutorizado(String dni) {
        return autorizados.contains(dni);
    }

    public List<String> obtenerAutorizados() {
        return autorizados;
    }

    @Override
    public String toString() {
        return "Empresa: " + razonSocial + " (CUIT: " + cuit + ")";
    }
}
