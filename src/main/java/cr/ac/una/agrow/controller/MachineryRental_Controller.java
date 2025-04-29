package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.machineryRental;
import cr.ac.una.agrow.service.machineryR_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("rent")
public class MachineryRental_Controller {

    @Autowired
    private machineryR_Service service;

    @GetMapping("list")
    public String listadoAlquiler(Model model){

        List<machineryRental> rents = service.getAll();

        System.out.println(rents.get(0).getRenterName());
        model.addAttribute("validate",null);
        model.addAttribute("rents",rents);

        return "machineryRental/list_machineryR";
    }

}
