package cr.ac.una.agrow.controller.harvest;

import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.service.HarvestService;
import cr.ac.una.agrow.service.producer.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/harvests")
public class HarvestController {

    @Autowired
    private HarvestService service;

    private ProducerService producerService;
    private int currentId;
    private static final int PAGE_SIZE = 5;

    public HarvestController() {
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
    public String updateHarvest(@RequestParam("idHarvest") Integer idHarvest,
            @RequestParam("typeC") String type,
            @RequestParam("dateC") LocalDate date,
            @RequestParam("totalC") String totalStr,
            @RequestParam("descriptionC") String description,
            @RequestParam("stateC") String quality,
            @RequestParam("destinyC") String destiny,
            @RequestParam("id_producer") int codeProd,
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

        Harvest harvest = service.getById(idHarvest);
        harvest.setTypeHarvest(type);
        harvest.setDateHarvested(date);
        harvest.setQuantityHarvested(Integer.parseInt(totalStr));
        harvest.setDescription(description);
        harvest.setQuality(quality);
        harvest.setDestiny(destiny);
        harvest.setRegisteredHarvest(true);
        harvest.setId_producer(codeProd);
        
        if (idHarvest == null || (harvest = service.getById(idHarvest)) == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró la cosecha para editar");
            return "redirect:/harvests/list";
        }

        service.save(harvest);

        return "redirect:/harvests/list";
    }

    @GetMapping("/list")
    public String listHarvests(Model model,
            @RequestParam(value = "stateC", required = false) String state,
            @RequestParam(value = "destinyC", required = false) String destiny,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page) {

        // Create a Pageable object for pagination
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Harvest> harvests;

        // Apply filters with pagination
        if (state != null && !state.isEmpty()) {
            harvests = service.getHarvestByQualityPaged(state, pageable);
            model.addAttribute("stateC", state);
        } else if (destiny != null && !destiny.isEmpty()) {
            harvests = service.getHarvestByDestinyPaged(destiny, pageable);
            model.addAttribute("destinyC", destiny);
        } else {
            harvests = service.getAllPaged(pageable);
        }

        // Add pagination data to model
        model.addAttribute("harvestList", harvests.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", harvests.getTotalPages());
        model.addAttribute("totalItems", harvests.getTotalElements());

        return "list_harvest";
    }

    @GetMapping("/page")
    public String paginate(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String stateC,
            @RequestParam(required = false) String destinyC,
            Model model) {
        int pageSize = 5; // O el tamaño que uses para tu paginación
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Harvest> harvestPage;

        // Aplicar filtros si existen
        if (stateC != null && !stateC.isEmpty()) {
            harvestPage = service.getHarvestByQualityPaged(stateC, pageable);
            model.addAttribute("stateC", stateC); // Mantener estado del filtro
        } else if (destinyC != null && !destinyC.isEmpty()) {
            harvestPage = service.getHarvestByDestinyPaged(destinyC, pageable);
            model.addAttribute("destinyC", destinyC); // Mantener estado del filtro
        } else {
            harvestPage = service.getAllPaged(pageable);
        }

        // Usar harvestList para coincidir con el nombre en list_harvest.html
        model.addAttribute("harvestList", harvestPage.getContent());

        // También puede ser útil incluir la información de paginación
        model.addAttribute("currentPage", page);

        return "fragments/harvest_table :: harvestTable";
    }

    @PostMapping("/delete")
    public String deleteHarvest(@RequestParam("idHarvest") int idHarvest, RedirectAttributes redirectAttributes) {
        Harvest harvest = service.getById(idHarvest);
        if (harvest != null) {
            service.delete(harvest);
            redirectAttributes.addFlashAttribute("mensaje", "Cosecha eliminada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró la cosecha con ID " + idHarvest);
        }
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
