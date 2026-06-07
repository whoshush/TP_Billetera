package ar.edu.ungs.billetera;

public class RentaFija extends Inversion {
    private double tasaInteres;

    public RentaFija(String id, double monto, String fecha, int plazo, String tipo, double tasaInteres, Cuenta origen, boolean aprobada) {
        super(id, monto, fecha, plazo, tipo, origen, aprobada);
        this.tasaInteres = tasaInteres;
    }

    public double getTasaInteres() {
        return tasaInteres;
    }

    @Override
    public double calcularResultado() {
        int dias = getDiasTranscurridos();
        double ganancia = getMonto() * (tasaInteres / 365) * dias;

        if (esPrecancelado()) {
            return aplicarFactorRentabilidadCuenta(ganancia / 2);
        }

        return aplicarFactorRentabilidadCuenta(ganancia);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RentaFija: tasaInteres=").append(tasaInteres)
          .append(" | ").append(obtenerDetalle());
        return sb.toString();
    }
}
