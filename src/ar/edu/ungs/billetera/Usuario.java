package ar.edu.ungs.billetera;

import java.util.HashMap;
import java.util.Map;

public class Usuario {
    private String dni;
    private String nombre;
    private String direccion;
    private Map<String, Cuenta> cuentas;

    public Usuario(String dni, String nombre, String direccion) {
        this.dni = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.cuentas = new HashMap<>();
    }

    public void agregarCuenta(Cuenta cuenta) {
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

    // Aplica el uso explícito de StringBuilder requerido por la cátedra
    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usuario: ").append(nombre)
          .append(" (DNI: ").append(dni).append(")")
          .append(" - Dirección: ").append(direccion);
        return sb.toString();
    }
}