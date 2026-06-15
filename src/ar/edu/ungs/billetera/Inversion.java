package ar.edu.ungs.billetera;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Inversion extends Operacion {
    private static int contadorId = 0;

    protected int plazo;
    private String tipo;
    private boolean precancelada;
    private boolean vencida;
    private Cuenta origen;

    protected static String generarSiguienteId() {
        contadorId++;
        return String.valueOf(contadorId);
    }

    public Inversion(String id, double monto, String fecha, int plazo, String tipo, Cuenta origen, boolean aprobada) {
        super(id, monto, fecha, aprobada);
        if (plazo <= 0) {
            throw new IllegalArgumentException("El plazo debe ser positivo.");
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de inversión es obligatorio.");
        }

        if (origen == null) {
            throw new IllegalArgumentException("La cuenta origen es obligatoria.");
        }
        this.plazo = plazo;
        this.tipo = tipo;
        this.precancelada = false;
        this.vencida = false;
        this.origen = origen;
    }

    @Override
    public boolean esInversion() {
        return true;
    }

    public void precancelar() {
        this.precancelada = true;
    }

    public boolean esPrecancelado() {
        return precancelada;
    }

    public boolean esVencida() {
        return vencida;
    }

    public boolean esPrecancelable() {
        return true;
    }

    public int getPlazo() {
        return plazo;
    }

    public Cuenta getOrigen() {
        return origen;
    }

    protected int getDiasTranscurridos() {
        LocalDate fechaInicio = LocalDate.parse(getFecha());
        LocalDate hoy = Utilitarios.hoy();
        long dias = ChronoUnit.DAYS.between(fechaInicio, hoy);

        if (dias > plazo) {
            dias = plazo;
        }
        return (int) Math.max(0, dias);
    }

    public abstract double calcularResultado();

    protected double aplicarFactorRentabilidadCuenta(double resultado) {
        return resultado * origen.factorRentabilidadInversion();
    }

    public boolean venceHoy() {
        if (precancelada || vencida) {
            return false;
        }
        LocalDate fechaInicio = LocalDate.parse(getFecha());
        return fechaInicio.plusDays(plazo).equals(Utilitarios.hoy());
    }

    @Override
    public boolean intentarPrecancelar(Cuenta cuenta, Usuario titular) {
        if (precancelada) {
            throw new IllegalArgumentException("La inversión ya se encuentra precancelada.");
        }
        if (vencida) {
            throw new IllegalArgumentException("La inversión ya venció.");
        }
        if (!esPrecancelable()) {
            throw new IllegalArgumentException("La inversión no es precancelable.");
        }
        precancelarEn(cuenta, titular);
        return true;
    }

    public void precancelarEn(Cuenta cuenta, Usuario titular) {
        precancelar();
        double gananciaFinal = calcularResultado();
        cuenta.depositar(getMonto() + gananciaFinal);
        titular.restarInvertido(getMonto());
    }

    public void procesarVencimiento(Cuenta cuenta, Usuario titular) {
        if (precancelada || vencida) {
            return;
        }
        vencida = true;
        double ganancia = calcularResultado();
        cuenta.depositar(getMonto() + ganancia);
        titular.restarInvertido(getMonto());
    }

    @Override
    public void intentarProcesarVencimientoHoy(Cuenta cuenta, Usuario titular) {
        if (venceHoy()) {
            procesarVencimiento(cuenta, titular);
        }
    }

    @Override
    public String obtenerTipo() {
        return "Inversión (" + tipo + ")";
    }

    @Override
    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Inversion:\n")
          .append("fecha: ").append(getFecha()).append("\n")
          .append("origen: ").append(origen.getDni()).append(" (").append(origen.getCvu()).append(")\n")
          .append("desc: ").append(obtenerTipo()).append("\n")
          .append("monto: ").append(getMonto()).append("\n")
          .append("plazo: ").append(plazo).append("\n")
          .append(isAprobada() ? "[Aprobado]" : "[Rechazado]");
        return sb.toString();
    }

    @Override
    public String toString() {
        return obtenerDetalle();
    }
}
