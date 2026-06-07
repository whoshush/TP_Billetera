package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Cuenta {
    private String cvu;
    private String alias;
    protected double saldo;
    private Map<String, Operacion> operaciones;
    private String dniTitular;

    public Cuenta(String cvu, String alias, String dniTitular) {
        if (cvu == null || cvu.trim().isEmpty()) {
            throw new IllegalArgumentException("El CVU es obligatorio.");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("El alias es obligatorio.");
        }
        if (dniTitular == null || dniTitular.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI del titular es obligatorio.");
        }
        this.cvu = cvu;
        this.alias = alias;
        this.saldo = 0.0;
        this.dniTitular = dniTitular;
        this.operaciones = new HashMap<>();
    }

    public String getCvu() {
        return cvu;
    }

    public String getAlias() {
        return alias;
    }

    public String getDni() {
        return dniTitular;
    }

    public double obtenerSaldo() {
        return saldo;
    }

    public void registrarOperacion(Operacion operacion) {
        operaciones.put(operacion.getId(), operacion);
    }

    public Map<String, Operacion> getOperaciones() {
        return operaciones;
    }

    public void depositar(double monto) {
        this.saldo += monto;
    }

    public void extraer(double monto) {
        this.saldo -= monto;
    }

    public abstract void validarOperacion(double monto) throws Exception;

    public void validarOperacionInversion(double monto) throws Exception {
        validarOperacion(monto);
    }

    public abstract void validarDeposito(double monto);

    public abstract String obtenerTipoCuenta();

    public boolean admiteFondoLiquidez() {
        return false;
    }

    public double factorRentabilidadInversion() {
        return 1.0;
    }

    public void realizarTransferencia(Cuenta destino, double monto, String id, String fecha, RegistroOperaciones registro) {
        Transferencia transf = new Transferencia(id, monto, fecha, this, destino, false);
        try {
            validarOperacion(monto);
            destino.validarDeposito(monto);
            extraer(monto);
            destino.depositar(monto);
            transf = new Transferencia(id, monto, fecha, this, destino, true);
        } catch (IllegalStateException e) {
            registrarOperacion(transf);
            destino.registrarOperacion(transf);
            registro.registrar(transf);
            throw e;
        } catch (Exception e) {
            registrarOperacion(transf);
            destino.registrarOperacion(transf);
            registro.registrar(transf);
            throw new IllegalArgumentException(e.getMessage());
        }
        registrarOperacion(transf);
        destino.registrarOperacion(transf);
        registro.registrar(transf);
    }

    public int realizarInversionRentaFija(double monto, int plazoDias, double tasa, String id, String fecha) throws Exception {
        validarOperacionInversion(monto);
        extraer(monto);
        RentaFija inv = new RentaFija(id, monto, fecha, plazoDias, "Renta Fija", tasa, this, true);
        registrarOperacion(inv);
        return Integer.parseInt(id);
    }

    public int realizarInversionDivisa(double monto, int plazoDias, String divisa, double tasa, String id, String fecha) throws Exception {
        validarOperacionInversion(monto);
        extraer(monto);
        Divisa inv = new Divisa(id, monto, fecha, plazoDias, "Divisa", divisa, tasa, this, true);
        registrarOperacion(inv);
        return Integer.parseInt(id);
    }

    public int realizarInversionLiquidez(double monto, int plazoDias, String id, String fecha) throws Exception {
        validarOperacionInversion(monto);
        extraer(monto);
        FondoEmpresarial inv = new FondoEmpresarial(id, monto, fecha, plazoDias, "Liquidez", this, true);
        registrarOperacion(inv);
        return Integer.parseInt(id);
    }

    public void precancelarInversion(String id, Usuario titular) {
        Operacion op = operaciones.get(id);
        if (op == null) {
            throw new IllegalArgumentException("La operación no existe en esta cuenta.");
        }
        if (!op.esInversion()) {
            throw new IllegalArgumentException("El ID corresponde a una transferencia, no a una inversión.");
        }
        op.intentarPrecancelar(this, titular);
    }

    public List<String> consultarHistorial() {
        List<String> historial = new ArrayList<>();
        for (Operacion op : operaciones.values()) {
            historial.add(op.obtenerDetalle());
        }
        return historial;
    }

    public int obtenerCantidadOperaciones() {
        return operaciones.size();
    }

    public String obtenerDescripcionConVolumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(obtenerTipoCuenta()).append("]: ")
          .append(alias).append(" (")
          .append(cvu).append(") - Operaciones: ")
          .append(operaciones.size());
        return sb.toString();
    }

    public String obtenerDescripcion() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(obtenerTipoCuenta()).append("]: ")
          .append(alias).append(" (")
          .append(cvu).append(")");
        return sb.toString();
    }

    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("CVU: ").append(cvu)
          .append(" | Alias: ").append(alias)
          .append(" | Saldo: $").append(saldo);
        return sb.toString();
    }

    @Override
    public String toString() {
        return obtenerDetalle();
    }
}
