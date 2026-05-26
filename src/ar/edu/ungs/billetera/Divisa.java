package ar.edu.ungs.billetera;

public class Divisa extends Inversion {
    private String monedaReferencia;
    private double tasa;

    public Divisa(String id, double monto, String fecha, int plazo, String tipo, String monedaReferencia, double tasa) {
        super(id, monto, fecha, plazo, tipo);
        this.monedaReferencia = monedaReferencia;
        this.tasa = tasa;
    }

    @Override
    public double calcularResultado() {
        if (esPrecancelado()) return 0;
        
        // Obtenemos la cotización actual usando la clase Utilitarios provista por la cátedra
        double cotizacionActual = Utilitarios.consultarCotizacion(monedaReferencia);
        
        // Lógica de ejemplo: (Monto * Tasa) * Cotización
        return (getMonto() * tasa) * cotizacionActual;
    }
}