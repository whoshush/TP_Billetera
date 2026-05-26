package ar.edu.ungs.billetera;

public class RentaFija extends Inversion {
    private double tasaInteres;

    public RentaFija(String id, double monto, String fecha, int plazo, String tipo, double tasaInteres) {
        super(id, monto, fecha, plazo, tipo);
        this.tasaInteres = tasaInteres;
    }

    @Override
    public double calcularResultado() {
        if (esPrecancelado()) {
            return 0; // Si se precancela, generalmente se pierde el interés (o se aplica una penalidad)
        }
        // Cálculo básico de interés simple: Monto * Tasa
        return getMonto() * tasaInteres; 
    }
}