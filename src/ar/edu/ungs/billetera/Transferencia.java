package ar.edu.ungs.billetera;

public class Transferencia extends Operacion {
    private static int contadorId = 0;

    private Cuenta origen;
    private Cuenta destino;

    private static String generarSiguienteId() {
        contadorId++;
        return "TR-" + contadorId;
    }

    public Transferencia(double monto, String fecha, Cuenta origen, Cuenta destino, boolean aprobada) {
        super(generarSiguienteId(), monto, fecha, aprobada);
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Las cuentas origen y destino son obligatorias.");
        }
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
