package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Usuario {
    private String dni;
    private String nombre;
    private String telefono;
    private String email;
    private Map<String, Cuenta> cuentas;
    private double totalInvertido;

    public Usuario(String dni, String nombre, String telefono, String email) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.cuentas = new HashMap<>();
        this.totalInvertido = 0.0;
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

    public Cuenta buscarCuentaPorAlias(String alias) {
        for (Cuenta cuenta : cuentas.values()) {
            if (cuenta.getAlias().equals(alias)) {
                return cuenta;
            }
        }
        return null;
    }

    public static Cuenta buscarCuentaEnSistema(Collection<Usuario> usuarios, String cvu) {
        for (Usuario usuario : usuarios) {
            Cuenta cuenta = usuario.buscarCuenta(cvu);
            if (cuenta != null) {
                return cuenta;
            }
        }
        return null;
    }

    public static String buscarCvuPorAlias(Collection<Usuario> usuarios, String alias) {
        for (Usuario usuario : usuarios) {
            Cuenta cuenta = usuario.buscarCuentaPorAlias(alias);
            if (cuenta != null) {
                return cuenta.getCvu();
            }
        }
        return null;
    }

    public static boolean aliasDisponible(Collection<Usuario> usuarios, String alias) {
        return buscarCvuPorAlias(usuarios, alias) == null;
    }

    public void sumarInvertido(double monto) {
        totalInvertido += monto;
    }

    public void restarInvertido(double monto) {
        totalInvertido -= monto;
        if (totalInvertido < 0) {
            totalInvertido = 0;
        }
    }

    public double obtenerTotalInvertido() {
        return totalInvertido;
    }

    public List<String> obtenerCuentasFormateadas() {
        final List<String> listaCuentas = new ArrayList<>();
        cuentas.values().forEach(new Consumer<Cuenta>() {
            @Override
            public void accept(Cuenta cuenta) {
                listaCuentas.add(cuenta.obtenerDescripcion());
            }
        });
        return listaCuentas;
    }

    public List<String> consultarHistorial() {
        List<String> historial = new ArrayList<>();
        for (Cuenta cuenta : cuentas.values()) {
            historial.addAll(cuenta.consultarHistorial());
        }
        return historial;
    }

    public String crearCuentaRegular(String alias) {
        String cvu = Utilitarios.generarSiguienteCvu();
        CuentaRegular cuenta = new CuentaRegular(cvu, alias, dni);
        agregarCuenta(cuenta);
        return cvu;
    }

    public String crearCuentaPremium(String alias, double depositoInicial) {
        CuentaPremium.validarDepositoInicial(depositoInicial);
        String cvu = Utilitarios.generarSiguienteCvu();
        CuentaPremium cuenta = new CuentaPremium(cvu, alias, dni);
        cuenta.depositar(depositoInicial);
        agregarCuenta(cuenta);
        return cvu;
    }

    public String crearCuentaCorporativa(String alias, Empresa empresa) {
        String cvu = Utilitarios.generarSiguienteCvu();
        CuentaCorporativa cuenta = new CuentaCorporativa(cvu, alias, dni, empresa);
        agregarCuenta(cuenta);
        return cvu;
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
