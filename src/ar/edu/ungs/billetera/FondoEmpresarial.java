package ar.edu.ungs.billetera;

public class FondoEmpresarial extends Inversion {
    private static final String ACTIVO_FLE = "FLE";
    private static final double TASA_BASE = 0.08;
    private double cotizacionFLE;

    public FondoEmpresarial(double monto, String fecha, int plazo, String tipo, Cuenta origen, boolean aprobada) {
        super( monto, fecha, plazo, tipo, origen, aprobada);
        if (monto < 20000000) {
            throw new IllegalArgumentException("El Fondo de Liquidez requiere un monto mínimo de $20.000.000.");
        }
        this.cotizacionFLE = Utilitarios.consultarCotizacion(ACTIVO_FLE);
    }

    @Override
    public double calcularResultado() {
        double cotizacionActualFLE = Utilitarios.consultarCotizacion(ACTIVO_FLE);
        int dias = getDiasTranscurridos();
        double rendimiento = getMonto() * (TASA_BASE + cotizacionActualFLE) / 365 * dias;
        if (esPrecancelado()) {
            return aplicarFactorRentabilidadCuenta(rendimiento / 2);
        }
        return aplicarFactorRentabilidadCuenta(rendimiento);
    }

    @Override
    public boolean esPrecancelable() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FondoEmpresarial: activo=").append(ACTIVO_FLE)
          .append(" | tasaBase=").append(TASA_BASE)
          .append(" | cotizacionFLE=").append(cotizacionFLE)
          .append(" | ").append(obtenerDetalle());
        return sb.toString();
    }
}
