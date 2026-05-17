package ar.edu.ungs.billetera;

public class CuentaPremium extends Cuenta {

    public CuentaPremium(String cvu, String alias, String dniTitular) {
        super(cvu, alias, dniTitular);
    }

    @Override
    public void validarOperacion(double monto) throws Exception {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (this.saldo < monto) {
            throw new Exception("Cuenta Premium: Saldo insuficiente.");
        }
    }
}