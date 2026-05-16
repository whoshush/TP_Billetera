package ar.edu.ungs.billetera;

public abstract class Inversion extends Operacion {
    protected int plazo; // En días
    private String tipo;
    private boolean precancelada;

    public Inversion(String id, double monto, String fecha, int plazo, String tipo) {
        super(id, monto, fecha);
        this.plazo = plazo;
        this.tipo = tipo;
        this.precancelada = false;
    }

    public void precancelar() {
        this.precancelada = true;
    }

    public boolean esPrecancelado() {
        return precancelada;
    }

    // Método polimórfico crucial: cada subtipo de inversión calcula su retorno distinto
    public abstract double calcularResultado();

    @Override
    public String obtenerTipo() {
        return "Inversión (" + tipo + ")";
    }
}