package cr.ac.una.agrow.controller.harvest;

import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.service.harvest.HarvestService;
import cr.ac.una.agrow.service.producer.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/harvests")
public class HarvestController {

    @Autowired
    private HarvestService service;

    private ProducerService producerService;
    private int currentId;
    
    public HarvestController(){
        this.producerService = new ProducerService();
    }

    @GetMapping("/form")
    public String formHarvest(Model model) {
        List<Producer> prodList = producerService.getAllProducers();

        model.addAttribute("producers", prodList);
        return "form_harvest";
    }

    @GetMapping("/edit")
    public String editHarvest(@RequestParam("idHarvest") int id, Model model) {
        this.currentId = id;
        Harvest harvest = service.getById(id);
        List<Producer> prodList = producerService.getAllProducers();

        model.addAttribute("producers", prodList);
        model.addAttribute("harvest", harvest);
        return "edit_harvest";
    }

    @PostMapping("/update")
    public String updateHarvest(
            @RequestParam("typeC") String type,
            @RequestParam("dateC") LocalDate date,
            @RequestParam("totalC") String totalStr,
            @RequestParam("descriptionC") String description,
            @RequestParam("stateC") String quality,
            @RequestParam("destinyC") String destiny,
            @RequestParam("producerId") int codeProd,
            RedirectAttributes redirectAttributes) {

        if (!totalStr.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("error", "El campo 'Total' debe contener solo números.");
            return "redirect:/harvests/edit?idHarvest=" + currentId;
        }

        if (date.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha no puede ser en el futuro.");
            return "redirect:/harvests/edit?idHarvest=" + currentId;
        }

        if (type.length() > 50 || description.length() > 250) {
            redirectAttributes.addFlashAttribute("error", "Tipo máx. 50 caracteres y descripción máx. 250.");
            return "redirect:/harvests/edit?idHarvest=" + currentId;
        }

        Harvest harvest = service.getById(currentId);
        harvest.setTypeHarvest(type);
        harvest.setDateHarvested(date);
        harvest.setQuantityHarvested(Integer.parseInt(totalStr));
        harvest.setDescription(description);
        harvest.setQuality(quality);
        harvest.setDestiny(destiny);
        harvest.setRegisteredHarvest(true);
        harvest.setId_producer(codeProd);

        service.save(harvest);

        return "redirect:/harvests/list";
    }

    @GetMapping("/list")
    public String listHarvests(Model model,
            @RequestParam(value = "stateC", required = false) String state,
            @RequestParam(value = "destinyC", required = false) String destiny) {

        List<Harvest> harvestList = service.getAll();

        if (state != null && !state.isEmpty()) {
            harvestList = service.getHarvestByQuality(state);
        } else if (destiny != null && !destiny.isEmpty()) {
            harvestList = service.getHarvestByDestiny(destiny);
        } else {
            harvestList = service.getAll();
        }

        model.addAttribute("harvestList", harvestList);
        return "list_harvest";
    }

    @PostMapping("/delete")
    public String deleteHarvest(@RequestParam("idHarvest") int idHarvest, RedirectAttributes redirectAttributes) {
        service.delete(idHarvest);
        redirectAttributes.addFlashAttribute("mensaje", "Cosecha eliminada exitosamente.");
        return "redirect:/harvests/list";
    }

    @PostMapping("/save")
    public String saveHarvest(
            @RequestParam("typeC") String type,
            @RequestParam("dateC") LocalDate date,
            @RequestParam("totalC") String totalStr,
            @RequestParam("descriptionC") String description,
            @RequestParam("stateC") String quality,
            @RequestParam("destinyC") String destiny,
            @RequestParam("producerId") String codeProd,
            RedirectAttributes redirectAttributes) {

        if (!totalStr.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("error", "El campo 'Total' debe contener solo números.");
            return "redirect:/harvests/form";
        }

        if (date.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha no puede ser en el futuro.");
            return "redirect:/harvests/form";
        }

        if (type.length() > 50 || description.length() > 250 || destiny.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Tipo máx. 50, descripción máx. 250, destino máx. 100.");
            return "redirect:/harvests/form";
        }

        Harvest harvest = new Harvest();
        harvest.setTypeHarvest(type);
        harvest.setDateHarvested(date);
        harvest.setQuantityHarvested(Integer.parseInt(totalStr));
        harvest.setDescription(description);
        harvest.setQuality(quality);
        harvest.setDestiny(destiny);
        harvest.setRegisteredHarvest(true);
        harvest.setId_producer(Integer.parseInt(codeProd));
        
        service.save(harvest);

        redirectAttributes.addFlashAttribute("mensaje", "Cosecha registrada correctamente.");
        return "redirect:/harvests/form";
    }
}
