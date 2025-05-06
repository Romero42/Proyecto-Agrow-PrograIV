package cr.ac.una.agrow.controller.harvest;

import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.service.HarvestService;
import cr.ac.una.agrow.service.Producer_Service;
import cr.ac.una.agrow.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/harvests")
public class HarvestController {

    private static final Logger LOG = Logger.getLogger(HarvestController.class.getName());

    @Autowired
    private HarvestService service;

    @Autowired
    private Producer_Service producerService;

    @Autowired(required = false)
    private SaleService saleService;

    private static final int PAGE_SIZE = 5;
    private static final int MAX_VISIBLE_PAGINATION_LINKS = 5;

    @GetMapping("/form")
    public String formHarvest(Model model) {
        Page<Producer> producerPage = producerService.findAll(PageRequest.of(0, Integer.MAX_VALUE));
        List<Producer> prodList = producerPage.getContent();

        model.addAttribute("producers", prodList);
        model.addAttribute("activeModule", "harvests");
        model.addAttribute("activePage", "add");
        model.addAttribute("harvest", new Harvest());
        return "form_harvest";
    }

    @PostMapping("/save")
    public String saveHarvest(
            @ModelAttribute Harvest harvest,
            @RequestParam(value = "id_producer", required = false, defaultValue = "0") Integer producerId,
            RedirectAttributes redirectAttributes) {

        // Validaciones básicas
        if (harvest.getTypeHarvest() == null || harvest.getTypeHarvest().trim().isEmpty() || harvest.getTypeHarvest().length() > 50) {
            redirectAttributes.addFlashAttribute("error", "Tipo de cosecha inválido (requerido, máx 50).");
            redirectAttributes.addFlashAttribute("harvest", harvest);
            return "redirect:/harvests/form";
        }
        if (harvest.getDateHarvested() == null || harvest.getDateHarvested().isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha es requerida y no puede ser en el futuro.");
            redirectAttributes.addFlashAttribute("harvest", harvest);
            return "redirect:/harvests/form";
        }
        if (harvest.getQuantityHarvested() <= 0) {
            redirectAttributes.addFlashAttribute("error", "El total cosechado debe ser mayor a cero.");
            redirectAttributes.addFlashAttribute("harvest", harvest);
            return "redirect:/harvests/form";
        }
        if (harvest.getQuality() == null || harvest.getQuality().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La calidad es requerida.");
            redirectAttributes.addFlashAttribute("harvest", harvest);
            return "redirect:/harvests/form";
        }
        if (harvest.getDestiny() == null || harvest.getDestiny().trim().isEmpty() || harvest.getDestiny().length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Destino inválido (requerido, máx 100).");
            redirectAttributes.addFlashAttribute("harvest", harvest);
            return "redirect:/harvests/form";
        }
        if (harvest.getDescription() == null || harvest.getDescription().trim().isEmpty() || harvest.getDescription().length() > 250) {
            redirectAttributes.addFlashAttribute("error", "Descripción inválida (requerida, máx 250).");
            redirectAttributes.addFlashAttribute("harvest", harvest);
            return "redirect:/harvests/form";
        }
        if (producerId == null || producerId <= 0 || producerService.getById(producerId) == null) {
            redirectAttributes.addFlashAttribute("error", "El productor seleccionado no existe o es inválido.");
            redirectAttributes.addFlashAttribute("harvest", harvest);
            return "redirect:/harvests/form";
        }

        harvest.setId_producer(producerId);
        harvest.setRegisteredHarvest(true);
        harvest.setAvailableQuantity(harvest.getQuantityHarvested());

        if (service.save(harvest)) {
            redirectAttributes.addFlashAttribute("mensaje", "Cosecha registrada correctamente.");
            return "redirect:/harvests/list";
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la cosecha.");
            redirectAttributes.addFlashAttribute("harvest", harvest);
            return "redirect:/harvests/form";
        }
    }

    @GetMapping("/edit")
    public String editHarvest(@RequestParam("idHarvest") int id, Model model, RedirectAttributes redirectAttributes) {
        Harvest harvest = service.getById(id);
        if (harvest == null) {
            redirectAttributes.addFlashAttribute("error", "Cosecha no encontrada (ID: " + id + ")");
            return "redirect:/harvests/list";
        }

        // Asegurarnos que no hay valores nulos en el objeto harvest
        if (harvest.getTypeHarvest() == null) {
            harvest.setTypeHarvest("");
        }
        if (harvest.getQuality() == null) {
            harvest.setQuality("");
        }
        if (harvest.getDestiny() == null) {
            harvest.setDestiny("");
        }
        if (harvest.getDescription() == null) {
            harvest.setDescription("");
        }
        if (harvest.getDateHarvested() == null) {
            harvest.setDateHarvested(LocalDate.now());
        }

        boolean hasSales = harvest.getAvailableQuantity() < harvest.getQuantityHarvested();
        Page<Producer> producerPage = producerService.findAll(PageRequest.of(0, Integer.MAX_VALUE));
        List<Producer> prodList = producerPage.getContent();

        model.addAttribute("producers", prodList);
        model.addAttribute("harvest", harvest);
        model.addAttribute("hasSales", hasSales);
        model.addAttribute("activeModule", "harvests");
        model.addAttribute("activePage", "edit");
        return "edit_harvest";
    }

    @PostMapping("/update")
    public String updateHarvest(
            @ModelAttribute Harvest harvest,
            @RequestParam(value = "id_producer", required = false, defaultValue = "0") Integer producerId,
            RedirectAttributes redirectAttributes) {

        // Validaciones básicas
        if (harvest.getIdHarvest() <= 0) {
            redirectAttributes.addFlashAttribute("error", "ID de cosecha inválido para actualizar.");
            return "redirect:/harvests/list";
        }
        Harvest existing = service.getById(harvest.getIdHarvest());
        if (existing == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró la cosecha original (ID: " + harvest.getIdHarvest() + ").");
            return "redirect:/harvests/list";
        }
        if (harvest.getTypeHarvest() == null || harvest.getTypeHarvest().trim().isEmpty() || harvest.getTypeHarvest().length() > 50) {
            redirectAttributes.addFlashAttribute("error", "Tipo de cosecha inválido.");
            return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
        }
        if (harvest.getDateHarvested() == null || harvest.getDateHarvested().isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha es requerida y no puede ser futura.");
            return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
        }
        if (harvest.getQuantityHarvested() <= 0) {
            redirectAttributes.addFlashAttribute("error", "El total cosechado debe ser mayor a cero.");
            return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
        }

        boolean hasSales = existing.getAvailableQuantity() < existing.getQuantityHarvested();
        if (hasSales) {
            int sold = existing.getQuantityHarvested() - existing.getAvailableQuantity();
            if (harvest.getQuantityHarvested() < sold) {
                redirectAttributes.addFlashAttribute("error",
                        "El nuevo total (" + harvest.getQuantityHarvested() + ") no puede ser menor que lo ya vendido (" + sold + ").");
                return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
            }
            harvest.setAvailableQuantity(harvest.getQuantityHarvested() - sold);
        } else {
            harvest.setAvailableQuantity(harvest.getQuantityHarvested());
        }

        if (harvest.getQuality() == null || harvest.getQuality().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La calidad es requerida.");
            return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
        }
        if (harvest.getDestiny() == null || harvest.getDestiny().trim().isEmpty() || harvest.getDestiny().length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Destino inválido.");
            return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
        }
        if (harvest.getDescription() == null || harvest.getDescription().trim().isEmpty() || harvest.getDescription().length() > 250) {
            redirectAttributes.addFlashAttribute("error", "Descripción inválida.");
            return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
        }
        if (producerId == null || producerId <= 0 || producerService.getById(producerId) == null) {
            redirectAttributes.addFlashAttribute("error", "El productor seleccionado no existe o es inválido.");
            return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
        }

        harvest.setId_producer(producerId);
        harvest.setRegisteredHarvest(existing.isRegisteredHarvest());

        if (service.save(harvest)) {
            redirectAttributes.addFlashAttribute("mensaje", "Cosecha actualizada correctamente.");
            return "redirect:/harvests/list";
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cosecha.");
            return "redirect:/harvests/edit?idHarvest=" + harvest.getIdHarvest();
        }
    }

    @GetMapping("/list")
    public String listHarvests(Model model,
            @RequestParam(value = "quality", required = false) String quality,
            @RequestParam(value = "destiny", required = false) String destiny,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Harvest> harvestsPage = service.getFilteredHarvestsPaged(quality, destiny, pageable);
        prepareModelForView(model, harvestsPage, quality, destiny, page);
        model.addAttribute("activeModule", "harvests");
        model.addAttribute("activePage", "list");
        return "list_harvest";
    }

    @GetMapping("/table")
    public String getHarvestTable(Model model,
            @RequestParam(value = "quality", required = false) String quality,
            @RequestParam(value = "destiny", required = false) String destiny,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Harvest> harvestsPage = service.getFilteredHarvestsPaged(quality, destiny, pageable);
        prepareModelForView(model, harvestsPage, quality, destiny, page);
        return "harvest/table_harvest :: harvestListContent";
    }

    @PostMapping("/delete")
    public String deleteHarvest(@RequestParam("idHarvest") int idHarvest, RedirectAttributes redirectAttributes) {
        Harvest harvest = service.getById(idHarvest);
        if (harvest != null) {
            try {
                service.delete(harvest);
                redirectAttributes.addFlashAttribute("mensaje",
                        "Cosecha (ID: " + idHarvest + ") y ventas asociadas eliminadas.");
            } catch (RuntimeException e) {
                LOG.log(Level.SEVERE, "Error eliminando cosecha ID " + idHarvest, e);
                redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró la cosecha ID " + idHarvest);
        }
        return "redirect:/harvests/list";
    }

    private void prepareModelForView(Model model, Page<Harvest> pageData,
            String quality, String destiny, int page) {
        int totalPages = pageData.getTotalPages();
        int start = Math.max(0, page - MAX_VISIBLE_PAGINATION_LINKS / 2);
        int end = Math.min(totalPages - 1, start + MAX_VISIBLE_PAGINATION_LINKS - 1);

        if (totalPages > MAX_VISIBLE_PAGINATION_LINKS && (end - start + 1) < MAX_VISIBLE_PAGINATION_LINKS) {
            if (start == 0) {
                end = MAX_VISIBLE_PAGINATION_LINKS - 1;
            } else if (end == totalPages - 1) {
                start = totalPages - MAX_VISIBLE_PAGINATION_LINKS;
            }
        }
        if (totalPages == 0) {
            start = 0;
            end = -1;
        }

        model.addAttribute("harvestList", pageData.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", pageData.getTotalElements());
        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);
        model.addAttribute("quality", quality);
        model.addAttribute("destiny", destiny);
        if (pageData.isEmpty()) {
            model.addAttribute("validate", (quality != null || destiny != null)
                    ? "No se encontraron cosechas con esos filtros."
                    : "No hay cosechas registradas.");
        }
    }
}
