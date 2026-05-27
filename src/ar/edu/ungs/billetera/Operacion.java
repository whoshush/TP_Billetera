// Operacion.java
package ar.edu.ungs.billetera;

public abstract class Operacion {
    private String id;
    protected double monto;
    private String fecha;
    private boolean aprobada;

    public Operacion(String id, double monto, String fecha) {
        this.id = id;
        this.monto = monto;
        this.fecha = fecha;
        this.aprobada = false;
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

    public void setAprobada(boolean estado) { 
        this.aprobada = estado; 
    }

    public boolean isAprobada() { 
        return aprobada; 
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