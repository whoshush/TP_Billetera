package ar.edu.ungs.billetera;

public class Transferencia extends Operacion {
    private Cuenta origen;
    private Cuenta destino;

    public Transferencia(String id, double monto, String fecha, Cuenta origen, Cuenta destino, boolean aprobada) {
        super(id, monto, fecha, aprobada);
        this.origen = origen;
        this.destino = destino;
    }

    @Override
    public String obtenerTipo() {
        return "Transferencia";
    }

    @Override
    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transferencia:\n")
          .append("fecha: ").append(getFecha()).append("\n")
          .append("origen: ").append(origen.getDni()).append(" (").append(origen.getCvu()).append(")\n")
          .append("destino: ").append(destino.getDni()).append(" (").append(destino.getCvu()).append(")\n")
          .append("monto: ").append(getMonto()).append("\n")
          .append(isAprobada() ? "[Aprobado]" : "[Rechazado]");
        return sb.toString();
    }

    @Override
    public String toString() {
        return obtenerDetalle();
    }
}
