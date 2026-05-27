package ar.edu.ungs.billetera;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class Billetera implements IBilletera {
    
    private Map<String, Usuario> usuarios;
    private Map<String, Empresa> empresas;
    private Map<String, Operacion> operacionesGlobales;
    private Map<String, Cuenta> cuentasPorCvu;
    private Map<String, String> aliasACvu;
    private Map<String, List<String>> autorizadosPorEmpresa;

    public Billetera() {
        this.usuarios = new HashMap<>();
        this.empresas = new HashMap<>();
        this.operacionesGlobales = new HashMap<>();
        this.autorizadosPorEmpresa = new HashMap<>();
        this.cuentasPorCvu = new HashMap<>();
        this.aliasACvu = new HashMap<>();
    }

    @Override
    public void registrarUsuario(String dni, String nombre, String telefono, String email) {
        if (dni == null || dni.trim().isEmpty() || nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI y el nombre son campos obligatorios.");
        }
        if (usuarios.containsKey(dni)) {
            throw new IllegalArgumentException("El usuario con DNI " + dni + " ya se encuentra registrado.");
        }
        Usuario nuevoUsuario = new Usuario(dni, nombre, telefono, email);
        usuarios.put(dni, nuevoUsuario);
    }

    @Override
    public void registrarEmpresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
        if (cuit == null || cuit.trim().isEmpty() || nombreFantasia == null || nombreFantasia.trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIT y el nombre de fantasía son obligatorios.");
        }
        if (empresas.containsKey(cuit)) {
            throw new IllegalArgumentException("La empresa con CUIT " + cuit + " ya está registrada.");
        }

        Empresa nuevaEmpresa = new Empresa(cuit, nombreFantasia);
        empresas.put(cuit, nuevaEmpresa);
        autorizadosPorEmpresa.put(cuit, new ArrayList<>());
    }

    @Override
    public void agregarPersonaAutorizada(String cuitEmpresa, String dniAutorizado) {
        if (!empresas.containsKey(cuitEmpresa)) {
            throw new IllegalArgumentException("La empresa con CUIT " + cuitEmpresa + " no existe.");
        }
        
        List<String> autorizados = autorizadosPorEmpresa.get(cuitEmpresa);
        if (autorizados.contains(dniAutorizado)) {
            throw new IllegalArgumentException("La persona con DNI " + dniAutorizado + " ya está autorizada.");
        }
        
        autorizados.add(dniAutorizado);
    }

    @Override
    public String crearCuentaRegular(String dniUsuario, String alias) {
        if (!usuarios.containsKey(dniUsuario)) {
            throw new IllegalArgumentException("El usuario no está registrado.");
        }
        if (aliasACvu.containsKey(alias)) {
            throw new IllegalArgumentException("Alias ya existe");
        }
        
        Usuario u = usuarios.get(dniUsuario);
        String cvu = Utilitarios.generarSiguienteCvu();
        
        CuentaRegular nuevaCuenta = new CuentaRegular(cvu, alias, dniUsuario);
        aliasACvu.put(alias, cvu);
        cuentasPorCvu.put(cvu, nuevaCuenta);
        u.agregarCuenta(nuevaCuenta);
        
        return cvu;
    }

    @Override
    public String crearCuentaPremium(String dniUsuario, String alias, double depositoInicial) {
        if (!usuarios.containsKey(dniUsuario)) {
            throw new IllegalArgumentException("El usuario no está registrado.");
        }
        if (depositoInicial < 500000) {
            throw new IllegalArgumentException("El depósito inicial no cumple con el mínimo requerido para Cuenta Premium.");
        }
        if (aliasACvu.containsKey(alias)) {
            throw new IllegalArgumentException("Alias ya existe");
        }
        
        Usuario u = usuarios.get(dniUsuario);
        String cvu = Utilitarios.generarSiguienteCvu();
        
        CuentaPremium nuevaCuenta = new CuentaPremium(cvu, alias, dniUsuario);
        nuevaCuenta.depositar(depositoInicial);
        cuentasPorCvu.put(cvu, nuevaCuenta);
        aliasACvu.put(alias, cvu);
        u.agregarCuenta(nuevaCuenta);
        
        return cvu;
    }

    @Override
    public String crearCuentaCorporativa(String dniUsuario, String alias, String cuitEmpresa) {
        if (!empresas.containsKey(cuitEmpresa)) {
            throw new IllegalArgumentException("La empresa no está registrada.");
        }
        if (!usuarios.containsKey(dniUsuario)) {
            throw new IllegalArgumentException("El usuario no está registrado.");
        }
        
        List<String> autorizados = autorizadosPorEmpresa.get(cuitEmpresa);
        if (!autorizados.contains(dniUsuario)) {
            throw new IllegalArgumentException("El usuario no está autorizado para esta empresa.");
        }
        if (aliasACvu.containsKey(alias)) {
            throw new IllegalArgumentException("Alias ya existe");
        }
        
        Usuario u = usuarios.get(dniUsuario);
        Empresa emp = empresas.get(cuitEmpresa);
        String cvu = Utilitarios.generarSiguienteCvu();
        
        CuentaCorporativa nuevaCuenta = new CuentaCorporativa(cvu, alias, dniUsuario, emp);
        u.agregarCuenta(nuevaCuenta);
        aliasACvu.put(alias, cvu);
        cuentasPorCvu.put(cvu, nuevaCuenta);
        
        return cvu;
    }

    @Override
    public void realizarTransferencia(String cvuOrigen, String cvuDestino, double monto) {
        if (cvuOrigen.equals(cvuDestino)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo.");
        }
        
        Cuenta origen = encontrarCuentaPorCvu(cvuOrigen);
        Cuenta destino = encontrarCuentaPorCvu(cvuDestino);
        
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("La cuenta de origen o destino no existe.");
        }
        
        String idOperacion = "TR-" + (operacionesGlobales.size() + 1);
        String fechaActual = Utilitarios.hoy().toString();

        Transferencia transf = new Transferencia(idOperacion, monto, fechaActual, origen, destino);

        try {
            origen.validarOperacion(monto);
            origen.extraer(monto);
            destino.depositar(monto);
            transf.setAprobada(true);
        } catch (Exception e) {
            transf.setAprobada(false);
            origen.registrarOperacion(transf);
            destino.registrarOperacion(transf);
            operacionesGlobales.put(idOperacion, transf);
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new IllegalArgumentException(e.getMessage());
        }

        origen.registrarOperacion(transf);
        destino.registrarOperacion(transf);
        operacionesGlobales.put(idOperacion, transf);
    }

    @Override
    public int realizarInversionRentaFija(String dni, String cvu, double monto, int plazoDias) {
        if (plazoDias <= 0) {
            throw new IllegalArgumentException("El plazo debe ser mayor a cero.");
        }
        Cuenta cuenta = validarCuentaDeUsuario(dni, cvu);
        try {
            cuenta.validarOperacion(monto);
            cuenta.extraer(monto);
            
            int idNumerico = operacionesGlobales.size() + 1;
            String idOperacion = String.valueOf(idNumerico);
            String fecha = Utilitarios.hoy().toString();
            
            RentaFija inv = new RentaFija(idOperacion, monto, fecha, plazoDias, "Renta Fija", 0.20, cuenta);
            inv.setAprobada(true);
            
            cuenta.registrarOperacion(inv);
            operacionesGlobales.put(idOperacion, inv);
            
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
        Cuenta cuenta = validarCuentaDeUsuario(dni, cvu);
        try {
            cuenta.validarOperacion(monto);
            cuenta.extraer(monto);
            
            int idNumerico = operacionesGlobales.size() + 1;
            String idOperacion = String.valueOf(idNumerico);
            String fecha = Utilitarios.hoy().toString();
            
            Divisa inv = new Divisa(idOperacion, monto, fecha, plazoDias, "Divisa", divisa, tasa, cuenta);
            inv.setAprobada(true);
            
            cuenta.registrarOperacion(inv);
            operacionesGlobales.put(idOperacion, inv);
            
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
        
        Cuenta cuenta = validarCuentaDeUsuario(dni, cvu);
        if (!(cuenta instanceof CuentaCorporativa)) {
            throw new IllegalArgumentException("Solo las cuentas corporativas pueden invertir en el Fondo de Liquidez.");
        }

        try {
            cuenta.validarOperacion(monto);
            cuenta.extraer(monto);
            
            int idNumerico = operacionesGlobales.size() + 1;
            String idOperacion = String.valueOf(idNumerico);
            String fecha = Utilitarios.hoy().toString();
            
            FondoEmpresarial inv = new FondoEmpresarial(idOperacion, monto, fecha, plazoDias, "Liquidez", cuenta);
            inv.setAprobada(true);
            
            cuenta.registrarOperacion(inv);
            operacionesGlobales.put(idOperacion, inv);
            
            return idNumerico;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void precancelarInversion(String dni, String cvu, int idInversion) {
        Cuenta cuenta = validarCuentaDeUsuario(dni, cvu);
        String idStr = String.valueOf(idInversion);
        
        Operacion op = cuenta.getOperaciones().get(idStr);
        if (op == null) {
            throw new IllegalArgumentException("La operación no existe en esta cuenta.");
        }
        
        if (op instanceof Inversion) {
            Inversion inv = (Inversion) op;
            if (inv.esPrecancelado()) {
                throw new IllegalArgumentException("La inversión ya se encuentra precancelada.");
            }
            if (!inv.esPrecancelable()) {
                throw new IllegalArgumentException("La inversión no es precancelable.");
            }
            
            inv.precancelar();
            double gananciaFinal = inv.calcularResultado();
            cuenta.depositar(inv.getMonto() + gananciaFinal);
        } else {
            throw new IllegalArgumentException("El ID corresponde a una transferencia, no a una inversión.");
        }
    }

    @Override
    public String consultarCvu(String alias) {
        if (!aliasACvu.containsKey(alias)) {
            throw new IllegalArgumentException("El alias no existe.");
        }
        return aliasACvu.get(alias);
    }

    @Override
    public List<String> obtenerCuentas(String dniUsuario) {
        if (!usuarios.containsKey(dniUsuario)) {
            throw new IllegalArgumentException("El usuario con DNI " + dniUsuario + " no existe.");
        }
        List<String> listaCuentas = new ArrayList<>();
        Usuario u = usuarios.get(dniUsuario);
        
        for (Cuenta c : u.getCuentas().values()) {
            String tipo = "Regular";
            if (c instanceof CuentaPremium) tipo = "Premium";
            else if (c instanceof CuentaCorporativa) tipo = "Corporativa";
            
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(tipo).append("]: ")
              .append(c.getAlias()).append(" (")
              .append(c.getCvu()).append(")");
            listaCuentas.add(sb.toString());
        }
        return listaCuentas;
    }

    @Override
    public double obtenerSaldoDisponible(String cvu) {
        Cuenta c = encontrarCuentaPorCvu(cvu);
        if (c == null) {
            throw new IllegalArgumentException("La cuenta con CVU " + cvu + " no existe.");
        }
        return c.obtenerSaldo();
    }

    @Override
    public List<String> consultarHistorialGlobal() {
        List<String> historial = new ArrayList<>();
        for (Operacion op : operacionesGlobales.values()) {
            historial.add(op.obtenerDetalle());
        }
        return historial;
    }

    @Override
    public List<String> consultarHistorialCuenta(String cvu) {
        Cuenta c = encontrarCuentaPorCvu(cvu);
        if (c == null) {
            throw new IllegalArgumentException("La cuenta con CVU " + cvu + " no existe.");
        }
        
        List<String> historial = new ArrayList<>();
        for (Operacion op : c.getOperaciones().values()) {
            historial.add(op.obtenerDetalle());
        }
        return historial;
    }

    @Override
    public List<String> consultarHistorialUsuario(String dniUsuario) {
        if (!usuarios.containsKey(dniUsuario)) {
            throw new IllegalArgumentException("El usuario no existe.");
        }
        
        Usuario u = usuarios.get(dniUsuario);
        List<String> historial = new ArrayList<>();
        
        for (Cuenta c : u.getCuentas().values()) {
            for (Operacion op : c.getOperaciones().values()) {
                historial.add(op.obtenerDetalle());
            }
        }
        return historial;
    }

    @Override
    public double obtenerTotalInvertido(String dniUsuario) {
        if (!usuarios.containsKey(dniUsuario)) {
            throw new IllegalArgumentException("El usuario no existe.");
        }
        
        Usuario u = usuarios.get(dniUsuario);
        double total = 0.0;
        
        for (Cuenta c : u.getCuentas().values()) {
            for (Operacion op : c.getOperaciones().values()) {
                if (op instanceof Inversion) {
                    Inversion inv = (Inversion) op;
                    if (!inv.esPrecancelado()) {
                        total += inv.getMonto();
                    }
                }
            }
        }
        return total;
    }

    @Override
    public List<String> cuentasConMayorVolumen(int cantidadTop) {
        if (cantidadTop <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        
        List<Cuenta> todasLasCuentas = new ArrayList<>();
        for (Usuario u : usuarios.values()) {
            todasLasCuentas.addAll(u.getCuentas().values());
        }
        
        Collections.sort(todasLasCuentas, new Comparator<Cuenta>() {
            @Override
            public int compare(Cuenta c1, Cuenta c2) {
                return Integer.compare(c2.getOperaciones().size(), c1.getOperaciones().size());
            }
        });
        
        List<String> resultado = new ArrayList<>();
        int limite = Math.min(cantidadTop, todasLasCuentas.size());
        
        for (int i = 0; i < limite; i++) {
            Cuenta c = todasLasCuentas.get(i);
            String tipo = "Regular";
            if (c instanceof CuentaPremium) tipo = "Premium";
            else if (c instanceof CuentaCorporativa) tipo = "Corporativa";
            
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(tipo).append("]: ")
              .append(c.getAlias()).append(" (")
              .append(c.getCvu()).append(") - Operaciones: ")
              .append(c.getOperaciones().size());
            resultado.add(sb.toString());
        }
        
        return resultado;
    }

    private Cuenta encontrarCuentaPorCvu(String cvu) {
        return cuentasPorCvu.get(cvu);
    }

    private Cuenta validarCuentaDeUsuario(String dni, String cvu) {
        if (!usuarios.containsKey(dni)) {
            throw new IllegalArgumentException("El usuario con DNI " + dni + " no existe.");
        }
        Usuario u = usuarios.get(dni);
        Cuenta c = u.buscarCuenta(cvu);
        if (c == null) {
            throw new IllegalArgumentException("La cuenta con CVU " + cvu + " no pertenece al usuario.");
        }
        return c;
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
        sb.append(operacionesGlobales.size()).append(" operaciones registradas.\n");
        
        sb.append("\n=== FIN DEL ESTADO ===");
        return sb.toString();
    }
}