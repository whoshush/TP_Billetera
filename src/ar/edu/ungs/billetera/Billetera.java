package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Billetera implements IBilletera {

    private Map<String, Usuario> usuarios;
    private Map<String, Empresa> empresas;
    private RegistroOperaciones registroOperaciones;

    public Billetera() {
        this.usuarios = new HashMap<>();
        this.empresas = new HashMap<>();
        this.registroOperaciones = new RegistroOperaciones();
    }

    @Override
    public void registrarUsuario(String dni, String nombre, String telefono, String email) {
        if (usuarios.containsKey(dni)) {
            throw new IllegalArgumentException("El usuario con DNI " + dni + " ya se encuentra registrado.");
        }
        usuarios.put(dni, new Usuario(dni, nombre, telefono, email));
    }

    @Override
    public void registrarEmpresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
        if (empresas.containsKey(cuit)) {
            throw new IllegalArgumentException("La empresa con CUIT " + cuit + " ya está registrada.");
        }
        empresas.put(cuit, new Empresa(cuit, nombreFantasia, telefono, email, nombreContacto));
    }

    @Override
    public void agregarPersonaAutorizada(String cuitEmpresa, String dniAutorizado) {
        Empresa empresa = empresas.get(cuitEmpresa);
        if (empresa == null) {
            throw new IllegalArgumentException("La empresa con CUIT " + cuitEmpresa + " no existe.");
        }
        empresa.agregarAutorizado(dniAutorizado);
    }

    @Override
    public String crearCuentaRegular(String dniUsuario, String alias) {
        Usuario usuario = obtenerUsuario(dniUsuario);
        if (!Usuario.aliasDisponible(usuarios.values(), alias)) {
            throw new IllegalArgumentException("Alias ya existe");
        }
        return usuario.crearCuentaRegular(alias);
    }

    @Override
    public String crearCuentaPremium(String dniUsuario, String alias, double depositoInicial) {
        Usuario usuario = obtenerUsuario(dniUsuario);
        if (!Usuario.aliasDisponible(usuarios.values(), alias)) {
            throw new IllegalArgumentException("Alias ya existe");
        }
        return usuario.crearCuentaPremium(alias, depositoInicial);
    }

    @Override
    public String crearCuentaCorporativa(String dniUsuario, String alias, String cuitEmpresa) {
        Empresa empresa = empresas.get(cuitEmpresa);
        if (empresa == null) {
            throw new IllegalArgumentException("La empresa no está registrada.");
        }
        Usuario usuario = obtenerUsuario(dniUsuario);
        if (!empresa.estaAutorizado(dniUsuario)) {
            throw new IllegalArgumentException("El usuario no está autorizado para esta empresa.");
        }
        if (!Usuario.aliasDisponible(usuarios.values(), alias)) {
            throw new IllegalArgumentException("Alias ya existe");
        }
        return usuario.crearCuentaCorporativa(alias, empresa);
    }

    @Override
    public void realizarTransferencia(String cvuOrigen, String cvuDestino, double monto) {
        if (cvuOrigen.equals(cvuDestino)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo.");
        }

        Cuenta origen = buscarCuenta(cvuOrigen);
        Cuenta destino = buscarCuenta(cvuDestino);

        if (origen == null || destino == null) {
            throw new IllegalArgumentException("La cuenta de origen o destino no existe.");
        }

        String idOperacion = registroOperaciones.generarIdTransferencia();
        String fechaActual = Utilitarios.hoy().toString();
        origen.realizarTransferencia(destino, monto, idOperacion, fechaActual, registroOperaciones);
    }

    @Override
    public int realizarInversionRentaFija(String dni, String cvu, double monto, int plazoDias) {
        if (plazoDias <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor a cero.");
        }
        Usuario usuario = obtenerUsuario(dni);
        Cuenta cuenta = validarCuentaDeUsuario(usuario, cvu);
        try {
            String idOperacion = registroOperaciones.generarIdInversion();
            String fecha = Utilitarios.hoy().toString();
            int idNumerico = cuenta.realizarInversionRentaFija(monto, plazoDias, 0.20, idOperacion, fecha);
            registroOperaciones.registrar(cuenta.obtenerOperacion(idOperacion));
            usuario.sumarInvertido(monto);
            return idNumerico;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public int realizarInversionDivisa(String dni, String cvu, double monto, int plazoDias, String divisa, double tasa) {
        if (plazoDias <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor a cero.");
        }
        Usuario usuario = obtenerUsuario(dni);
        Cuenta cuenta = validarCuentaDeUsuario(usuario, cvu);
        try {
            String idOperacion = registroOperaciones.generarIdInversion();
            String fecha = Utilitarios.hoy().toString();
            int idNumerico = cuenta.realizarInversionDivisa(monto, plazoDias, divisa, tasa, idOperacion, fecha);
            registroOperaciones.registrar(cuenta.obtenerOperacion(idOperacion));
            usuario.sumarInvertido(monto);
            return idNumerico;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public int realizarInversionLiquidez(String dni, String cvu, double monto, int plazoDias) {
        if (plazoDias <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor a cero.");
        }

        Usuario usuario = obtenerUsuario(dni);
        Cuenta cuenta = validarCuentaDeUsuario(usuario, cvu);
        if (!cuenta.admiteFondoLiquidez()) {
            throw new IllegalArgumentException("Solo las cuentas corporativas pueden invertir en el Fondo de Liquidez.");
        }

        try {
            String idOperacion = registroOperaciones.generarIdInversion();
            String fecha = Utilitarios.hoy().toString();
            int idNumerico = cuenta.realizarInversionLiquidez(monto, plazoDias, idOperacion, fecha);
            registroOperaciones.registrar(cuenta.obtenerOperacion(idOperacion));
            usuario.sumarInvertido(monto);
            return idNumerico;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void precancelarInversion(String dni, String cvu, int idInversion) {
        Usuario usuario = obtenerUsuario(dni);
        Cuenta cuenta = validarCuentaDeUsuario(usuario, cvu);
        cuenta.precancelarInversion(String.valueOf(idInversion), usuario);
    }

    @Override
    public String consultarCvu(String alias) {
        String cvu = Usuario.buscarCvuPorAlias(usuarios.values(), alias);
        if (cvu == null) {
            throw new IllegalArgumentException("El alias no existe.");
        }
        return cvu;
    }

    @Override
    public List<String> obtenerCuentas(String dniUsuario) {
        return obtenerUsuario(dniUsuario).obtenerCuentasFormateadas();
    }

    @Override
    public double obtenerSaldoDisponible(String cvu) {
        Cuenta cuenta = buscarCuenta(cvu);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta con CVU " + cvu + " no existe.");
        }
        return cuenta.obtenerSaldo();
    }

    @Override
    public List<String> consultarHistorialGlobal() {
        return registroOperaciones.consultarHistorialGlobal();
    }

    @Override
    public List<String> consultarHistorialCuenta(String cvu) {
        Cuenta cuenta = buscarCuenta(cvu);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta con CVU " + cvu + " no existe.");
        }
        return cuenta.consultarHistorial();
    }

    @Override
    public List<String> consultarHistorialUsuario(String dniUsuario) {
        return obtenerUsuario(dniUsuario).consultarHistorial();
    }

    @Override
    public double obtenerTotalInvertido(String dniUsuario) {
        return obtenerUsuario(dniUsuario).obtenerTotalInvertido();
    }

    @Override
    public List<String> cuentasConMayorVolumen(int cantidadTop) {
        if (cantidadTop <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        final List<Cuenta> todasLasCuentas = new ArrayList<>();
        usuarios.values().forEach(usuario -> todasLasCuentas.addAll(usuario.getCuentas().values()));

        Collections.sort(todasLasCuentas, new Comparator<Cuenta>() {
            @Override
            public int compare(Cuenta c1, Cuenta c2) {
                return Integer.compare(c2.obtenerCantidadOperaciones(), c1.obtenerCantidadOperaciones());
            }
        });

        List<String> resultado = new ArrayList<>();
        int limite = Math.min(cantidadTop, todasLasCuentas.size());
        for (int i = 0; i < limite; i++) {
            resultado.add(todasLasCuentas.get(i).obtenerDescripcionConVolumen());
        }
        return resultado;
    }

    @Override
    public void procesarInversionesQueVencenHoy() {
        for (Usuario usuario : usuarios.values()) {
            for (Cuenta cuenta : usuario.getCuentas().values()) {
                for (Operacion op : cuenta.getOperaciones().values()) {
                    op.intentarProcesarVencimientoHoy(cuenta, usuario);
                }
            }
        }
    }

    private Usuario obtenerUsuario(String dni) {
        Usuario usuario = usuarios.get(dni);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario con DNI " + dni + " no existe.");
        }
        return usuario;
    }

    private Cuenta buscarCuenta(String cvu) {
        return Usuario.buscarCuentaEnSistema(usuarios.values(), cvu);
    }

    private Cuenta validarCuentaDeUsuario(Usuario usuario, String cvu) {
        Cuenta cuenta = usuario.buscarCuenta(cvu);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta con CVU " + cvu + " no pertenece al usuario.");
        }
        return cuenta;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADO INTERNO DE BILLETE.AR ===\n\n");

        sb.append("--- USUARIOS REGISTRADOS ---\n");
        for (Usuario u : usuarios.values()) {
            sb.append(u.toString()).append("\n");
        }

        sb.append("\n--- EMPRESAS REGISTRADAS ---\n");
        for (Empresa e : empresas.values()) {
            sb.append(e.toString()).append("\n");
        }

        sb.append("\n--- OPERACIONES TOTALES ---\n");
        sb.append(registroOperaciones.obtenerCantidadOperaciones()).append(" operaciones registradas.\n");

        sb.append("\n=== FIN DEL ESTADO ===");
        return sb.toString();
    }
}
