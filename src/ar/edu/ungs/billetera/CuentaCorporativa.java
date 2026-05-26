package ar.edu.ungs.billetera;

public class CuentaCorporativa extends Cuenta {
    private Empresa empresaAsociada;

    public CuentaCorporativa(String cvu, String alias, String dniTitular, Empresa empresaAsociada) {
        super(cvu, alias, dniTitular);
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
}