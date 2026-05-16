package ar.edu.ungs.billetera;

import java.util.HashMap;
import java.util.Map;

public abstract class Cuenta {
    private String cvu;
    private String alias;
    protected double saldo; // Se utiliza double para la compatibilidad con montos e intereses financieros
    private Map<String, Operacion> operaciones;

    public Cuenta(String cvu, String alias) {
        this.cvu = cvu;
        this.alias = alias;
        this.saldo = 0.0;
        this.operaciones = new HashMap<>();
    }

    public String getCvu() {
        return cvu;
    }

    public String getAlias() {
        return alias;
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

    // Métodos para modificar el saldo desde los procesos controlados del sistema
    public void depositar(double monto) {
        this.saldo += monto;
    }

    public void extraer(double monto) {
        this.saldo -= monto;
    }

    // Método polimórfico que cada subclase implementará según sus reglas
    public abstract void validarOperacion(double monto) throws Exception;

    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("CVU: ").append(cvu)
          .append(" | Alias: ").append(alias)
          .append(" | Saldo: $").append(saldo);
        return sb.toString();
    }
}