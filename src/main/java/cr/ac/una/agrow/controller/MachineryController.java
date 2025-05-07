package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.machinery.Machinery;
import cr.ac.una.agrow.jpa.MachineryRepository;
import cr.ac.una.agrow.service.MachineryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class MachineryController {

    @Autowired
    private MachineryService machineryService;
    @Autowired
    private MachineryRepository machineryRepository;

    @GetMapping("/lista")
    public String listado(Model model) {
        List<Machinery> maquinas = machineryService.getAllMachinery();

        model.addAttribute("titulo", "Listado de Maquinaria");
        model.addAttribute("cantidad", maquinas.size());
        model.addAttribute("maquinas", maquinas);

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

        ModelAndView modelAndView = new ModelAndView("index");

        try {
           
            LocalDate fechaAdquisicion = LocalDate.parse(diaAdquisicion);

            Machinery machinery = new Machinery(id, nombre, condicion, disponibilidad,
                    fechaAdquisicion, costoAlquiler, ubicacion, capacidadTrabajo);

            if (machineryRepository.existsById(id)) {
                modelAndView.addObject("error", "El ID ya existe, no se puede crear un nuevo registro con el mismo ID.");
            } else {
                machineryService.saveMachineryDefinitivo(machinery);
                modelAndView.addObject("mensaje", "Maquinaria agregada correctamente");
            }

        } catch (RuntimeException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (Exception e) {
            modelAndView.addObject("error", "Error inesperado al guardar: " + e.getMessage());
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

        List<Machinery> maquinas = machineryService.getByDisponibilidad(disponible);

        model.addAttribute("titulo", "Maquinaria " + (disponible ? "Disponible" : "No Disponible"));
        model.addAttribute("cantidad", maquinas.size());
        model.addAttribute("maquinas", maquinas);
        model.addAttribute("disponibilidadFiltro", disponible);

        return "machinery/lista_machinery";
    }

    @GetMapping("/buscarPorNombre")
    public String buscarPorNombre(
            @RequestParam(name = "nombre", required = false, defaultValue = "") String nombre,
            Model model) {

        List<Machinery> maquinas = machineryService.getByNombre(nombre);

        model.addAttribute("titulo", "Búsqueda por Nombre: " + nombre);
        model.addAttribute("cantidad", maquinas.size());
        model.addAttribute("maquinas", maquinas);
        model.addAttribute("nombreBuscado", nombre);

        return "machinery/lista_machinery";
    }

    @PostMapping("/gestionar")
    public String eliminar(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        try {
            boolean eliminado = machineryService.deleteMachinery(id);
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
        Optional<Machinery> machineryOpt = machineryService.getMachineryById(id);

        if (!machineryOpt.isPresent()) {
            model.addAttribute("mensaje", "Máquina no encontrada.");
            return "redirect:/lista";
        }

        model.addAttribute("maquinaria", machineryOpt.get());
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
        boolean actualizado = machineryService.updateMachinery(machinery) != null;
        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensaje", "La machinery se actualizó correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Hubo un error al actualizar la machinery.");
        }
        return "redirect:/lista";
    }

    @GetMapping("/detalles")
    public String verDetalle(@RequestParam("id") int id, Model model) {

        Optional<Machinery> maquina = machineryService.getMachineryById(id);

        if (!maquina.isPresent()) {
            model.addAttribute("error", "La máquina no existe.");
            return "redirect:/lista";
        }

        model.addAttribute("maquina", maquina.get());
        model.addAttribute("rental", false);
        model.addAttribute("titulo", "Detalles de la Máquina: " + maquina.get().getNombre());

        return "machinery/detail_machinery";
    }
}
