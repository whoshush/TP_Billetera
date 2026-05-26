package ar.edu.ungs.billetera;

public class RentaFija extends Inversion {
    private double tasaInteres;

    public RentaFija(String id, double monto, String fecha, int plazo, String tipo, double tasaInteres, Cuenta origen) {
        super(id, monto, fecha, plazo, tipo, origen);
        this.tasaInteres = tasaInteres;
    }

    @Override
    public double calcularResultado() {
        if (esPrecancelado()) return 0;
        return getMonto() * tasaInteres * getPlazo();
    }
}