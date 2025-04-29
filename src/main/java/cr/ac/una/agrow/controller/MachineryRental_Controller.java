package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.machinery.Machinery;
import cr.ac.una.agrow.domain.machineryRental;
import cr.ac.una.agrow.service.machinery.MachineryDB;
import cr.ac.una.agrow.service.machineryR_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        List<machineryRental> rents = service.getAll();
        String message = null;

        if(rents.isEmpty()){
            message = "No se puede listar alquileres";
        }

        model.addAttribute("validate", message);
        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("rents",rents);

        return "machineryRental/list_machineryR";
    }

    @GetMapping("saveView")
    public String saveView(Model model){

        LinkedList<Machinery> maquinas = machineryDB.listarMachinery();

        model.addAttribute("activeModule", "machineryRental");
        model.addAttribute("list", maquinas);
        return "machineryRental/add_rental";
    }

    @GetMapping("delete")
    public String delete(@RequestParam int id_machinaryrental, RedirectAttributes redirectAttributes){

        if(service.deleteById(id_machinaryrental)) {

            redirectAttributes.addFlashAttribute("mensaje", "El proceso fue exitoso");
        }else {

            redirectAttributes.addFlashAttribute("mensaje", "El proceso no fallo");
        }

        return "redirect:/rent/list";
    }

}
