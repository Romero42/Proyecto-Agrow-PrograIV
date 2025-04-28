package cr.ac.una.agrow.service.machinery;

import cr.ac.una.agrow.data.machinery.MachineryData;
import cr.ac.una.agrow.domain.machinery.Machinery;
import java.util.LinkedList;

/**
 *
 * @author andre
 */
public class MachineryDB {

    private final MachineryData machineryData = new MachineryData();

    public boolean agregarMachinery(Machinery machinery) {
        return machineryData.insertar(machinery);
    }
    
    public LinkedList<Machinery> listarMachinery() {
        return machineryData.obtenerMachinery();
    }
    
    public LinkedList<Machinery> filtrarPorDisponibilidad(boolean disponible) {
        LinkedList<Machinery> machineries = machineryData.obtenerMachinery();
        LinkedList<Machinery> resultado = new LinkedList<>();

        for (Machinery machinery : machineries) {
            if (machinery.getDisponibilidad() == disponible) {
                resultado.add(machinery);
            }
        }
        return resultado;
    }
    
    public LinkedList<Machinery> filtrarPorNombre(String nombre) {
        return machineryData.obtenerMachineryPorNombre(nombre);
    }
    
    public boolean eliminarMachinery(int id) {
        return machineryData.eliminar(id);
    }
    
    public boolean actualizarMachinery(Machinery machinery) {
        return machineryData.actualizar(machinery);
    }
    public Machinery obtenerMachineryPorId(int id) {
    return machineryData.obtenerMaquinaPorId(id); 
}

}