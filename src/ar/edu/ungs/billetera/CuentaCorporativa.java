package ar.edu.ungs.billetera;

public class CuentaCorporativa extends Cuenta {
    private Empresa empresaAsociada;

    public CuentaCorporativa(String cvu, String alias, String dniTitular, Empresa empresaAsociada) {
        super(cvu, alias, dniTitular);
        if (empresaAsociada == null) {
            throw new IllegalArgumentException("La empresa asociada es obligatoria.");
        }
        this.empresaAsociada = empresaAsociada;
    }

    public Empresa getEmpresaAsociada() {
        return empresaAsociada;
    }

    @Override
    public void validarOperacion(double monto) throws Exception {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (this.saldo < monto) {
            throw new Exception("Cuenta Corporativa: Fondos insuficientes de la empresa.");
        }
    }

    @Override
    public void validarDeposito(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
    }

    @Override
    public String obtenerTipoCuenta() {
        return "Corporativa";
    }

    @Override
    public boolean admiteFondoLiquidez() {
        return true;
    }

    @Override
    public double factorRentabilidadInversion() {
        return 1.05;
    }

    @Override
    public String toString() {
        return "Cuenta Corporativa (" + empresaAsociada.getRazonSocial() + "): " + obtenerDetalle();
    }
}
