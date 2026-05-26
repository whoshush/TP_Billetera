package ar.edu.ungs.billetera;

public class Divisa extends Inversion {
    private String monedaReferencia;
    private double tasa;

    public Divisa(String id, double monto, String fecha, int plazo, String tipo, String monedaReferencia, double tasa, Cuenta origen) {
        super(id, monto, fecha, plazo, tipo, origen);
        this.monedaReferencia = monedaReferencia;
        this.tasa = tasa;
    }

    @Override
    public double calcularResultado() {
        if (esPrecancelado()) return 0;
        double cotizacionActual = Utilitarios.consultarCotizacion(monedaReferencia);
        return (getMonto() * tasa * getPlazo()) * cotizacionActual;
    }
}