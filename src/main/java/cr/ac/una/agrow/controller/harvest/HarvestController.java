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
                               @RequestParam(value = "stateC", required = false) String quality,
                               @RequestParam(value = "destinyC", required = false) String destiny,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Harvest> harvestsPage; // Renombrado para claridad

        // Aplicar filtros y paginación
        if (quality != null && !quality.isEmpty()) {
            harvestsPage = service.getHarvestByQualityPaged(quality, pageable);
            model.addAttribute("stateC", quality);
        } else if (destiny != null && !destiny.isEmpty()) {
            harvestsPage = service.getHarvestByDestinyPaged(destiny, pageable);
            model.addAttribute("destinyC", destiny);
        } else {
            harvestsPage = service.getAllPaged(pageable);
        }

        // Añadir datos de paginación al modelo
        model.addAttribute("harvestList", harvestsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", harvestsPage.getTotalPages());
        model.addAttribute("totalItems", harvestsPage.getTotalElements());
        model.addAttribute("activeModule", "harvests"); // Para sidebar
        model.addAttribute("activePage", "list");     // Para sidebar

        return "list_harvest";
    }

    // Endpoint AJAX para paginación
    @GetMapping("/page")
    public String paginate(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String stateC,
                           @RequestParam(required = false) String destinyC,
                           Model model) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Harvest> harvestsPage; // Renombrado

        if (stateC != null && !stateC.isEmpty()) {
            harvestsPage = service.getHarvestByQualityPaged(stateC, pageable);
            model.addAttribute("stateC", stateC);
        } else if (destinyC != null && !destinyC.isEmpty()) {
            harvestsPage = service.getHarvestByDestinyPaged(destinyC, pageable);
            model.addAttribute("destinyC", destinyC);
        } else {
            harvestsPage = service.getAllPaged(pageable);
        }

        // Pasar datos necesarios para el fragmento
        model.addAttribute("harvestList", harvestsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", harvestsPage.getTotalPages());
        model.addAttribute("totalItems", harvestsPage.getTotalElements());
        model.addAttribute("stateC", stateC);
        model.addAttribute("destinyC", destinyC);


        // Devolver el fragmento que contiene la tabla Y la paginación
        return "list_harvest :: harvestListContent";
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
