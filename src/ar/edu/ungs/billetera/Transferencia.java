package ar.edu.ungs.billetera;

public class Transferencia extends Operacion {
    private Cuenta origen;
    private Cuenta destino;

    public Transferencia(String id, double monto, String fecha, Cuenta origen, Cuenta destino) {
        super(id, monto, fecha);
        this.origen = origen;
        this.destino = destino;
    }

    @Override
    public String obtenerTipo() {
        return "Transferencia";
    }

    @Override
    public String obtenerDetalle() {
        // Aprovechamos el StringBuilder requerido
        StringBuilder sb = new StringBuilder(super.obtenerDetalle());
        sb.append(" | Origen CVU: ").append(origen.getCvu())
          .append(" | Destino CVU: ").append(destino.getCvu());
        return sb.toString();
    }
}