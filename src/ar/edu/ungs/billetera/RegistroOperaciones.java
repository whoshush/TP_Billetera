package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RegistroOperaciones {
    private Map<String, Operacion> operacionesGlobales;

    public RegistroOperaciones() {
        this.operacionesGlobales = new HashMap<>();
    }

    public void registrar(Operacion operacion) {
        if (operacion == null) {
            throw new IllegalArgumentException("La operación a registrar no puede ser nula.");
        }
        operacionesGlobales.put(operacion.getId(), operacion);
    }

    public List<String> consultarHistorialGlobal() {
        List<String> historial = new ArrayList<>();
        Iterator<Operacion> it = operacionesGlobales.values().iterator();
        while (it.hasNext()) {
            historial.add(it.next().obtenerDetalle());
        }
        return historial;
    }

    public int obtenerCantidadOperaciones() {
        return operacionesGlobales.size();
    }
}
