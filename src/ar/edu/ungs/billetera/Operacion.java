// Operacion.java
package ar.edu.ungs.billetera;

public abstract class Operacion {
    private String id;
    protected double monto;
    private String fecha;
    private boolean aprobada;

    public Operacion(String id, double monto, String fecha, boolean aprobada) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la operación es obligatorio.");
        }
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de la operación es obligatoria.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        this.id = id;
        this.monto = monto;
        this.fecha = fecha;
        this.aprobada = aprobada;
    }

    public String getId() {
        return id;
    }

    public double getMonto() {
        return monto;
    }

    public String getFecha() {
        return fecha;
    }

    public boolean isAprobada() {
        return aprobada;
    }

    public boolean esInversion() {
        return false;
    }

    public boolean intentarPrecancelar(Cuenta cuenta, Usuario titular) {
        return false;
    }

    public void intentarProcesarVencimientoHoy(Cuenta cuenta, Usuario titular) {
    }

    public abstract String obtenerTipo();

    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append(obtenerTipo()).append(" | ID: ").append(id)
          .append(" | Monto: $").append(monto)
          .append(" | Fecha: ").append(fecha);
        return sb.toString();
    }

    @Override
    public String toString() {
        return obtenerDetalle();
    }
}
