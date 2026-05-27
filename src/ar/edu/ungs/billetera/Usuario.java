package ar.edu.ungs.billetera;

import java.util.HashMap;
import java.util.Map;

public class Usuario {
    private String dni;
    private String nombre;
    private String telefono;
    private String email;
    private Map<String, Cuenta> cuentas;

    public Usuario(String dni, String nombre, String telefono, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.cuentas = new HashMap<>();
    }

    public void agregarCuenta(Cuenta cuenta) {
        if (!cuenta.getDni().equals(this.dni)) {
            throw new IllegalArgumentException("El DNI de la cuenta no coincide con el usuario.");
        }
        cuentas.put(cuenta.getCvu(), cuenta);
    }

    public Cuenta buscarCuenta(String cvu) {
        return cuentas.get(cvu);
    }

    public Map<String, Cuenta> getCuentas() {
        return cuentas;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usuario: ").append(nombre)
          .append(" (DNI: ").append(dni).append(")")
          .append(" - Teléfono: ").append(telefono)
          .append(" - Email: ").append(email);
        return sb.toString();
    }

    @Override
    public String toString() {
        return obtenerDetalle();
    }
}