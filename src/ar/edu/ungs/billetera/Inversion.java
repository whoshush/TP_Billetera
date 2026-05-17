package ar.edu.ungs.billetera;

public abstract class Inversion extends Operacion {
    protected int plazo;
    private String tipo;
    private boolean precancelada;
    private Cuenta origen;

    public Inversion(String id, double monto, String fecha, int plazo, String tipo, Cuenta origen) {
        super(id, monto, fecha);
        this.plazo = plazo;
        this.tipo = tipo;
        this.precancelada = false;
        this.origen = origen;
    }

    public void precancelar() {
        this.precancelada = true;
    }

    public boolean esPrecancelado() {
        return precancelada;
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

    public abstract double calcularResultado();

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
}