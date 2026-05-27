package ar.edu.ungs.billetera;

public class FondoEmpresarial extends Inversion {

    public FondoEmpresarial(String id, double monto, String fecha, int plazo, String tipo, Cuenta origen) {
        super(id, monto, fecha, plazo, tipo, origen);
    }

    @Override
    public double calcularResultado() {
        int dias = getDiasTranscurridos();
        double ganancia = getMonto() * (0.08 / 365) * dias;
        if (esPrecancelado()) {
            return ganancia / 2;
        }
        return ganancia;
    }

    @Override
    public boolean esPrecancelable() {
        return false;
    }
}