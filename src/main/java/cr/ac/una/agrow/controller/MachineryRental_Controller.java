package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.machinery.Machinery;
import cr.ac.una.agrow.domain.machineryRental;
import cr.ac.una.agrow.service.machinery.MachineryDB;
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

@Controller
@RequestMapping("rent")
public class MachineryRental_Controller {

    private final MachineryDB machineryDB = new MachineryDB();

    @Autowired
    private machineryR_Service service;

    @GetMapping("list")
    public String listadoAlquiler(Model model){

        Pageable pageable = PageRequest.of(0, 5);
        Page<machineryRental> rentsPage = service.getAllPaginated(pageable);

        String message = null;

        if(rentsPage.isEmpty()){
            message = "No se puede listar alquileres";
        }

        model.addAttribute("validate", message);
        model.addAttribute("activeModule", "machineryRental");
       // model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentsPage.getTotalPages());
        model.addAttribute("rents",rentsPage.getContent());

        return "machineryRental/list_machineryR";
    }

    @GetMapping("pageCurrent")
    public String pageCurrent(Model model, @RequestParam(defaultValue = "0") int page){

        System.out.println("pageC  "+ page);
        Pageable pageable = PageRequest.of(page, 5);
        Page<machineryRental> rentsPage = service.getAllPaginated(pageable);

        String message = null;

        if(rentsPage.isEmpty()){
            message = "No se puede listar alquileres";
        }

        model.addAttribute("validate", message);
        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentsPage.getTotalPages());
        model.addAttribute("rents",rentsPage.getContent());

        return "machineryRental/table_machineryR";
    }

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

    @GetMapping("saveView")
    public String saveView(Model model){

        LinkedList<Machinery> maquinas = machineryDB.filtrarPorDisponibilidad(true);


        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("list", maquinas);
        return "machineryRental/add_rental";
    }

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

    @GetMapping("view")
    public String view(@RequestParam int id_machinaryrental, Model model){

        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("machinery", service.getById(id_machinaryrental));

        return "machineryRental/view_machineryR";
    }

    @GetMapping("edit")
    public String editR(RedirectAttributes redirectAttributes, @RequestParam String renterName, @RequestParam String address,
                       @RequestParam String contactNumber, @RequestParam LocalDate rentStartDay,  @RequestParam LocalDate rentFinalDay,
                       @RequestParam int machinery, @RequestParam int id_machinaryrental, @RequestParam int id_maquina){

        if(service.update(new machineryRental(id_machinaryrental, renterName, address, contactNumber, rentStartDay, rentFinalDay, machinery))){

            if(id_maquina != machinery){
                System.out.println("updateMa "+id_maquina);
                updateStatusMachine(id_maquina, true);
                updateStatusMachine(machinery, false);
            }

            redirectAttributes.addFlashAttribute("mensaje", "Se actualizo el alquiler");
        }else{
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el alquiler");
        }

        return "redirect:/rent/list";
    }

    @GetMapping("editView")
    public String editView(Model model, int id_machinaryrental, int id_maquina){

        LinkedList<Machinery> maquinas = machineryDB.filtrarPorDisponibilidad(true);
        maquinas.add(machineryDB.obtenerMachineryPorId(id_maquina));

        model.addAttribute("machinery", service.getById(id_machinaryrental));
        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("list", maquinas);

        return "machineryRental/update_machineryR";
    }

    @GetMapping("tableFilter")
    public String tableFilters(Model model, @RequestParam(required = false) LocalDate rentStartDay,
                               @RequestParam(required = false) LocalDate rentFinalDay, @RequestParam(required = false) Integer id_maquina){

        List<machineryRental> list = new ArrayList<>();
        String message = null;

        if(rentStartDay != null && rentFinalDay == null){

            list = service.dateRangeByStart(rentStartDay);

        }else if(rentStartDay != null && rentFinalDay != null){

            list = service.dateRange(rentStartDay, rentFinalDay);
        }else if(rentStartDay == null && rentFinalDay != null){

            list = service.dateRangeByFinal(rentFinalDay);
        }

        if(id_maquina != null){

            list.add(service.findByIdMaquina(id_maquina));
        }

        if(list.get(0) == null){
            list.clear();
            message = "No se puede listar alquileres";
        }

        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("validate", message);
        model.addAttribute("rents", list);

        return "machineryRental/table_machineryR";
    }

    @GetMapping("viewMaquina")
    public String viewMaquinaA(Model model, @RequestParam int id_maquina){
        Machinery m = machineryDB.obtenerMachineryPorId(id_maquina);

        model.addAttribute("maquina", m);
        model.addAttribute("rental", true);
        model.addAttribute("titulo", "Detalles de la Máquina: " + m.getNombre());

        return "machinery/detail_machinery";
    }

    public void updateStatusMachine(int id_machinery, boolean status){

        Machinery m = machineryDB.obtenerMachineryPorId(id_machinery);
        m.setDisponibilidad(status);
        machineryDB.actualizarMachinery(m);
    }
}
