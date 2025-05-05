package cr.ac.una.agrow.controller.machinery;

import cr.ac.una.agrow.domain.machinery.Machinery;
import cr.ac.una.agrow.service.machinery.MachineryDB;
import java.time.LocalDate;
import java.util.LinkedList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author andre
 */
@Controller
public class MachineryController {
   
    private final MachineryDB machineryDB = new MachineryDB();
    
    @GetMapping("/lista")
    public String listado(Model model) {
        LinkedList<Machinery> maquinas = machineryDB.listarMachinery();
    
        model.addAttribute("titulo", "Listado de Maquinaria");
        model.addAttribute("cantidad", maquinas.size());
        model.addAttribute("maquinas", maquinas); //
        

        return "machinery/lista_machinery";
    }
    
    @PostMapping("/guardar")
    public ModelAndView agregarMachinery(
        @RequestParam("id") int id,
        @RequestParam("nombre") String nombre,
        @RequestParam("condicion") String condicion,
        @RequestParam("disponibilidad") boolean disponibilidad,
        @RequestParam("diaAdquisicion") String diaAdquisicion,
        @RequestParam("costoAlquiler") double costoAlquiler,
        @RequestParam("ubicacion") String ubicacion,
        @RequestParam("capacidadTrabajo") String capacidadTrabajo) {

        // Esto me va a convertir el String de diaAdquisicion a LocalDate
        LocalDate fechaAdquisicion = LocalDate.parse(diaAdquisicion);

        Machinery machinery = new Machinery(id, nombre, condicion, disponibilidad, fechaAdquisicion, costoAlquiler, ubicacion, capacidadTrabajo);
        boolean resultado = machineryDB.agregarMachinery(machinery);

        ModelAndView modelAndView = new ModelAndView("index");
        if (resultado) {
            modelAndView.addObject("mensaje", "Machinery agregada correctamente.");
        } else {
            modelAndView.addObject("mensaje", "Error al agregar la machinery.");
        }
        return modelAndView;
    }

    @GetMapping({"/formulario", "/form", "/create"})
    public String formulario() {
        return "machinery/form_maquinaria";
    }
    
   @GetMapping("/buscarPorDisponibilidad")
public String buscarPorDisponibilidad(
    @RequestParam(required = false, defaultValue = "true") boolean disponible, 
    Model model) {
    
    LinkedList<Machinery> maquinas = machineryDB.filtrarPorDisponibilidad(disponible);

    model.addAttribute("titulo", "Maquinaria " + (disponible ? "Disponible" : "No Disponible"));
    model.addAttribute("cantidad", maquinas.size());
    model.addAttribute("maquinas", maquinas);
    model.addAttribute("disponibilidadFiltro", disponible);

    return "machinery/lista_machinery"; // Usar la misma vista
}

@GetMapping("/buscarPorNombre")
public String buscarPorNombre(
    @RequestParam(name = "nombre", required = false, defaultValue = "") String nombre, 
    Model model) {
    
    LinkedList<Machinery> maquinas = machineryDB.filtrarPorNombre(nombre);

    model.addAttribute("titulo", "Búsqueda por Nombre: " + nombre);
    model.addAttribute("cantidad", maquinas.size());
    model.addAttribute("maquinas", maquinas);
    model.addAttribute("nombreBuscado", nombre);

    return "machinery/lista_machinery"; // Usar la misma vista
}
    
    @PostMapping("/gestionar")
public String eliminar(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
    try {
        boolean eliminado = machineryDB.eliminarMachinery(id);
        if (eliminado) {
            redirectAttributes.addFlashAttribute("mensaje", "Máquina eliminada correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la máquina");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
    }
    return "redirect:/lista";
}
   @GetMapping("/editar")
public String editar(@RequestParam("id") int id, Model model) {
    // Versión optimizada del método
    Machinery machinery = machineryDB.listarMachinery()
        .stream()
        .filter(m -> m.getId() == id)
        .findFirst()
        .orElse(null);

    if (machinery == null) {
        model.addAttribute("mensaje", "Máquina no encontrada.");
        return "redirect:/lista";
    }
    
    model.addAttribute("maquinaria", machinery);
    return "machinery/editar_maquinaria";
}

    @PostMapping("/actualizar")
    public String actualizarMachinery(@RequestParam("id") int id, 
        @RequestParam("nombre") String nombre, 
        @RequestParam("condicion") String condicion, 
        @RequestParam("disponibilidad") boolean disponibilidad, 
        @RequestParam("diaAdquisicion") LocalDate diaAdquisicion, 
        @RequestParam("costoAlquiler") double costoAlquiler, 
        @RequestParam("ubicacion") String ubicacion, 
        @RequestParam("capacidadTrabajo") String capacidadTrabajo, 
        RedirectAttributes redirectAttributes) {
    
        Machinery machinery = new Machinery(id, nombre, condicion, disponibilidad, diaAdquisicion, costoAlquiler, ubicacion, capacidadTrabajo);
        boolean actualizado = machineryDB.actualizarMachinery(machinery);
        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensaje", "La machinery se actualizó correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Hubo un error al actualizar la machinery.");
        }
        return "redirect:/lista";
    }
    
    @GetMapping("/detalles")
    public String verDetalle(@RequestParam("id") int id, Model model) {
        
        Machinery maquina = machineryDB.obtenerMachineryPorId(id);

        if (maquina == null) {
            // Si no existe, redirigir a la lista con un mensaje de error
            model.addAttribute("error", "La máquina no existe.");
            return "redirect:/lista";
        }

        model.addAttribute("maquina", maquina);
        model.addAttribute("rental", false);
        model.addAttribute("titulo", "Detalles de la Máquina: " + maquina.getNombre());

        return "machinery/detail_machinery"; 
    }
}
