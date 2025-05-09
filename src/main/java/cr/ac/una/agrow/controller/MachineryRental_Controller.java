package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.Machinery;
import cr.ac.una.agrow.domain.machineryRental;
import cr.ac.una.agrow.service.MachineryService;
import cr.ac.una.agrow.service.machineryR_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
@Controller
@RequestMapping("rent")
public class MachineryRental_Controller {

    @Autowired
    private MachineryService machineryService;

    @Autowired
    private machineryR_Service service;

    //manda la pagina con los datos de la base de datos a la vista
    @GetMapping("list")
    public String listadoAlquiler(Model model){

        Pageable pageable = PageRequest.of(0, 5);
        Page<machineryRental> rentsPage = service.getAllPaginated(pageable);

        String message = null;

        if(rentsPage.isEmpty()){
            message = "No se puede listar alquileres";
        }

        model.addAttribute("validate", message);
        model.addAttribute("currentPage", 0);
        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("totalPages", rentsPage.getTotalPages());
        model.addAttribute("rents",rentsPage.getContent());

        return "machineryRental/list_machineryR";
    }

    //actualiza la pagina con los nuevos datos de la pagina seleccionada
    @GetMapping("pageCurrent")
    public String pageCurrent(Model model, @RequestParam(defaultValue = "0") int page){

        Pageable pageable = PageRequest.of(page, 5);
        Page<machineryRental> rentsPage = service.getAllPaginated(pageable);

        String message = null;

        if(rentsPage.isEmpty()){
            message = "No se puede listar alquileres";
        }

        model.addAttribute("validate", message);
        model.addAttribute("useFilter", false);
        model.addAttribute("pag", true);
        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentsPage.getTotalPages());
        model.addAttribute("rents",rentsPage.getContent());

        return "machineryRental/table_machineryR";
    }

    //guarda un alquiler y actualiza el estado de la maquina que se eligio
    @GetMapping("save")
    public String saveR(RedirectAttributes redirectAttributes, @RequestParam String renterName, @RequestParam String address,
                        @RequestParam String contactNumber, @RequestParam LocalDate rentStartDay,  @RequestParam LocalDate rentFinalDay,
                        @RequestParam int machinery){

        if(service.save(new machineryRental(renterName, address, contactNumber, rentStartDay, rentFinalDay, machinery))){

            updateStatusMachine(machinery, false);
            redirectAttributes.addFlashAttribute("mensaje", "Se agrego el alquiler");
        }else{
            redirectAttributes.addFlashAttribute("error", "No se pudo agregar el alquiler");
        }

        return "redirect:/rent/list";
    }

     //envia los datos de las maquinas disponibles a la vista de agregar
    @GetMapping("saveView")
    public String saveView(Model model) {

        LinkedList<Machinery> maquinas = new LinkedList<>(machineryService.getByDisponibilidad(true));

        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("list", maquinas);
        return "machineryRental/add_rental";
    }

    //borra un aluiler y actualiza el estado de la maquina
    @GetMapping("delete")
    public String deleteR(@RequestParam int id_machinaryrental, @RequestParam int id_maquina, RedirectAttributes redirectAttributes){

        if(service.deleteById(id_machinaryrental)) {

            updateStatusMachine(id_maquina, true);
            redirectAttributes.addFlashAttribute("mensaje", "El proceso fue exitoso");
        }else {

            redirectAttributes.addFlashAttribute("error", "El proceso fallo");
        }

        return "redirect:/rent/list";
    }

    //envia a la vista de informacion de un alquiler
    @GetMapping("view")
    public String view(@RequestParam int id_machinaryrental, Model model){

        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("machinery", service.getById(id_machinaryrental));

        return "machineryRental/view_machineryR";
    }

    //edita un alquiler en la bd y se actualiza el estado de la maquina si se elige otra
    @GetMapping("edit")
    public String editR(RedirectAttributes redirectAttributes, @RequestParam String renterName, @RequestParam String address,
                        @RequestParam String contactNumber, @RequestParam LocalDate rentStartDay,  @RequestParam LocalDate rentFinalDay,
                        @RequestParam int machinery, @RequestParam int id_machinaryrental, @RequestParam int id_maquina){

        if(service.update(new machineryRental(id_machinaryrental, renterName, address, contactNumber, rentStartDay, rentFinalDay, machinery))){

            if(id_maquina != machinery){

                updateStatusMachine(id_maquina, true);
                updateStatusMachine(machinery, false);
            }

            redirectAttributes.addFlashAttribute("mensaje", "Se actualizo el alquiler");
        }else{
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el alquiler");
        }

        return "redirect:/rent/list";
    }

    //envia y carga las maquinas disponible en la vista de actualizar
    @GetMapping("editView")
    public String editView(Model model,
            @RequestParam("id_machinaryrental") int idMachinaryRental,
            @RequestParam("id_maquina") int idMaquina) {

        try {
            // 1. Obtener máquinas disponibles
            List<Machinery> maquinas = new LinkedList<>(machineryService.getByDisponibilidad(true));

            // 2. Añadir la máquina actual (aunque no esté disponible)
            Machinery maquinaActual = machineryService.getMachineryById(idMaquina)
                    .orElseThrow(() -> new RuntimeException("Máquina no encontrada"));
            if (!maquinas.contains(maquinaActual)) {
                maquinas.add(maquinaActual);
            }

            // 3. Obtener el alquiler
            machineryRental rental = service.getById(idMachinaryRental);
            if (rental == null) {
                throw new RuntimeException("Alquiler no encontrado");
            }

            // 4. Agregar atributos al modelo
            model.addAttribute("machinery", rental);
            model.addAttribute("activeModule", "machineryRental");
            model.addAttribute("list", maquinas);

            return "machineryRental/update_machineryR";

        } catch (Exception e) {
            return "redirect:/rent/list?error=" + e.getMessage();
        }
    }

    //se envia una pagina dependiendo de el filtro solicitado
    @GetMapping("tableFilter")
    public String tableFilters(Model model, @RequestParam(required = false) LocalDate rentStartDay,
                               @RequestParam(required = false) LocalDate rentFinalDay,
                               @RequestParam(required = false) Integer id_maquina, @RequestParam(defaultValue = "0") int page){

        Pageable pageable = PageRequest.of(page, 5);
        Page<machineryRental> rentsPage = null;
        String message = null;
        List<machineryRental> machinery = new ArrayList<>();

        System.out.println("  ada  "+page);
        if(rentStartDay != null && rentFinalDay == null){

            rentsPage = service.dateRangeByStart(rentStartDay, pageable);

        }else if(rentStartDay != null && rentFinalDay != null){

            rentsPage = service.dateRange(rentStartDay, rentFinalDay, pageable);
        }else if(rentStartDay == null && rentFinalDay != null){

            rentsPage = service.dateRangeByFinal(rentFinalDay, pageable);
        }

        if (id_maquina != null) {
            machineryRental rent = service.findByIdMaquina(id_maquina);

            if (rent == null) {

                message = "No se encontró alquiler con ese código";
                model.addAttribute("rents", null);
            } else {

                model.addAttribute("useFilter", true);
                machinery.add(rent);
                model.addAttribute("rents", machinery);
            }

        } else if (rentsPage == null || rentsPage.isEmpty()) {

            message = "No se encontraron alquileres con los filtros aplicados";
            model.addAttribute("rents", null);
        } else {

            model.addAttribute("useFilter", true);
            model.addAttribute("rents", rentsPage.getContent());
            model.addAttribute("totalPages", rentsPage.getTotalPages());
            model.addAttribute("pag", true);
        }

        model.addAttribute("useFilter", true);
        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("validate", message);
        model.addAttribute("currentPage", page);

        return "machineryRental/table_machineryR";
    }

    //envia los datos de la maquina solicitada
    @GetMapping("viewMaquina")
    public String viewMaquinaA(Model model, @RequestParam int id_maquina) {
        Machinery m = machineryService.getMachineryById(id_maquina)
                .orElseThrow(() -> new NoSuchElementException("Máquina no encontrada con ID: " + id_maquina));


        model.addAttribute("maquina", m);
        model.addAttribute("rental", true);
        model.addAttribute("titulo", "Detalles de la Máquina: " + m.getNombre());

        System.out.println(m.getNombre());
        System.out.println("asdas "+m.getCostoAlquiler());
        return "machinery/detail_machinery";
    }

    //actualiza el estado de una maquina en la bd
    public void updateStatusMachine(int id_machinery, boolean status) {
        Machinery m = machineryService.getMachineryById(id_machinery)
                .orElseThrow(() -> new NoSuchElementException("Máquina no encontrada con ID: " + id_machinery));
        m.setDisponibilidad(status);
        machineryService.updateMachinery(m);
    }
}