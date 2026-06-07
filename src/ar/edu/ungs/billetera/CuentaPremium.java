package ar.edu.ungs.billetera;

public class CuentaPremium extends Cuenta {

    public CuentaPremium(String cvu, String alias, String dniTitular) {
        super(cvu, alias, dniTitular);
    }

    public static void validarDepositoInicial(double depositoInicial) {
        if (depositoInicial < 500000) {
            throw new IllegalArgumentException("El depósito inicial no cumple con el mínimo requerido para Cuenta Premium.");
        }
    }

    private static final double SALDO_MINIMO = 500000;

    @Override
    public void validarOperacion(double monto) throws Exception {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (this.saldo < monto) {
            throw new Exception("Cuenta Premium: Saldo insuficiente.");
        }
        if (this.saldo - monto < SALDO_MINIMO) {
            throw new Exception("Cuenta Premium: el saldo disponible no puede bajar de $500.000.");
        }
    }

    @Override
    public void validarOperacionInversion(double monto) throws Exception {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (this.saldo < monto) {
            throw new Exception("Cuenta Premium: Saldo insuficiente.");
        }
    }

    @Override
    public double factorRentabilidadInversion() {
        return 1.0;
    }

    @Override
    public void validarDeposito(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
    }

    @Override
    public String obtenerTipoCuenta() {
        return "Premium";
    }

    @Override
    public String toString() {
        return "Cuenta Premium: " + obtenerDetalle();
    }
}
