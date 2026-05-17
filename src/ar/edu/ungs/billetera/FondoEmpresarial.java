package ar.edu.ungs.billetera;

public class FondoEmpresarial extends Inversion {

    public FondoEmpresarial(String id, double monto, String fecha, int plazo, String tipo, Cuenta origen) {
        super(id, monto, fecha, plazo, tipo, origen);
    }

    @Override
    public double calcularResultado() {
        if (esPrecancelado()) return 0;
        return getMonto() * 0.08 * Utilitarios.consultarCotizacion("FLE");
    }

    @Override
    public boolean esPrecancelable() {
        return false;
    }
}