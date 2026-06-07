package ar.edu.ungs.billetera;

public class Divisa extends Inversion {
    private String monedaReferencia;
    private double tasa;
    private double cotizacionInicial;

    public Divisa(String id, double monto, String fecha, int plazo, String tipo, String monedaReferencia, double tasa, Cuenta origen, boolean aprobada) {
        super(id, monto, fecha, plazo, tipo, origen, aprobada);
        this.monedaReferencia = monedaReferencia;
        this.tasa = tasa;
        this.cotizacionInicial = Utilitarios.consultarCotizacion(monedaReferencia);
    }

    @Override
    public double calcularResultado() {
        int dias = getDiasTranscurridos();
        double cotizacionActual = Utilitarios.consultarCotizacion(monedaReferencia);

        double divisasCompradas = getMonto() / cotizacionInicial;
        double interesesEnDivisas = divisasCompradas * (tasa / 365) * dias;

        if (esPrecancelado()) {
            interesesEnDivisas /= 2;
        }

        double gananciaCapitalPesos = (divisasCompradas * cotizacionActual) - getMonto();
        double gananciaInteresesPesos = interesesEnDivisas * cotizacionActual;

        return aplicarFactorRentabilidadCuenta(gananciaCapitalPesos + gananciaInteresesPesos);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Divisa: moneda=").append(monedaReferencia)
          .append(" | tasa=").append(tasa)
          .append(" | cotizacionInicial=").append(cotizacionInicial)
          .append(" | ").append(obtenerDetalle());
        return sb.toString();
    }
}
