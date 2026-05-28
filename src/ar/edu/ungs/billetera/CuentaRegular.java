package ar.edu.ungs.billetera;

public class CuentaRegular extends Cuenta {

    public CuentaRegular(String cvu, String alias, String dniTitular) {
        super(cvu, alias, dniTitular);
    }

    @Override
    public void validarOperacion(double monto) throws Exception {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (this.saldo < monto) {
            throw new Exception("Saldo insuficiente para realizar la operación.");
        }
    }

    @Override
    public void depositar(double monto) {
        if (this.saldo + monto > 5000000) {
            throw new IllegalStateException("El saldo de la cuenta regular no puede superar los $5.000.000.");
        }
        super.depositar(monto);
    }
}