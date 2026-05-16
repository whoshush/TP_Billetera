package ar.edu.ungs.billetera;

public class FondoEmpresarial extends Inversion {

    public FondoEmpresarial(String id, double monto, String fecha, int plazo, String tipo) {
        super(id, monto, fecha, plazo, tipo);
    }

    @Override
    public double calcularResultado() {
        if (esPrecancelado()) return 0;
        // Supongamos un retorno fijo agresivo para el fondo empresarial a modo de ejemplo
        return getMonto() * 0.15; 
    }
}