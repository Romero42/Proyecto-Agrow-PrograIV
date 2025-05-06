
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
import org.springframework.dao.DataAccessException;

@Controller
@RequestMapping("/harvests")
public class HarvestController {
    private static final Logger LOG = Logger.getLogger(HarvestController.class.getName()); // Logger

    @Autowired
    private HarvestService service;

    @Autowired
    private Producer_Service producerService;

    @Autowired
    private SaleService saleService;

    private static final int PAGE_SIZE = 5;

    @GetMapping("/form")
    public String formHarvest(Model model) {

        Page<Producer> producerPage = producerService.findAll(PageRequest.of(0, Integer.MAX_VALUE));
        List<Producer> prodList = producerPage.getContent();

        model.addAttribute("producers", prodList);
        model.addAttribute("activeModule", "harvests");
        model.addAttribute("activePage", "add");
        return "form_harvest";
    }

    @GetMapping("/edit")
    public String editHarvest(@RequestParam("idHarvest") int id, Model model) {
        Harvest harvest = service.getById(id);
        if (harvest == null) {
            // Añadir mensaje de error si es necesario
            return "redirect:/harvests/list";
        }
        // Verificar si hay ventas asociadas para deshabilitar edición de cantidad total
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
    public String updateHarvest(@RequestParam("idHarvest") Integer idHarvest,
                                @RequestParam("typeC") String type,
                                @RequestParam("dateC") LocalDate date,
                                @RequestParam("totalC") String totalStr,
                                @RequestParam("descriptionC") String description,
                                @RequestParam("stateC") String quality,
                                @RequestParam("destinyC") String destiny,
                                @RequestParam("id_producer") int codeProd,
                                RedirectAttributes redirectAttributes) {

        // Validación de entrada básica
        if (idHarvest == null) {
            redirectAttributes.addFlashAttribute("error", "ID de cosecha inválido.");
            return "redirect:/harvests/list";
        }

        if (date.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha no puede ser en el futuro.");
            return "redirect:/harvests/edit?idHarvest=" + idHarvest;
        }

        if (type.length() > 50 || description.length() > 250 || destiny.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Verifique longitudes: Tipo (50), Descripción (250), Destino (100).");
            return "redirect:/harvests/edit?idHarvest=" + idHarvest;
        }

        Harvest harvest = service.getById(idHarvest);
        if (harvest == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró la cosecha para editar (ID: " + idHarvest + ")");
            return "redirect:/harvests/list";
        }

        // Validar si se intenta cambiar el total cuando ya hay ventas
        boolean hasSales = harvest.getAvailableQuantity() < harvest.getQuantityHarvested();
        int totalHarvested;

        if (!totalStr.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("error", "El campo 'Total' debe contener solo números enteros.");
            return "redirect:/harvests/edit?idHarvest=" + idHarvest;
        }
        try {
            totalHarvested = Integer.parseInt(totalStr);
            if (totalHarvested <= 0) {
                redirectAttributes.addFlashAttribute("error", "El campo 'Total' debe ser mayor a cero.");
                return "redirect:/harvests/edit?idHarvest=" + idHarvest;
            }
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "El valor de 'Total' no es un número entero válido.");
            return "redirect:/harvests/edit?idHarvest=" + idHarvest;
        }

        if (hasSales && totalHarvested != harvest.getQuantityHarvested()) {
            // Comprobar si el nuevo total es menor que lo ya vendido
            int quantityAlreadySold = harvest.getQuantityHarvested() - harvest.getAvailableQuantity();
            if (totalHarvested < quantityAlreadySold) {
                redirectAttributes.addFlashAttribute("error", "El nuevo total (" + totalHarvested + ") no puede ser menor que la cantidad ya vendida (" + quantityAlreadySold + ").");
                return "redirect:/harvests/edit?idHarvest=" + idHarvest;
            }
            // Si es mayor o igual a lo vendido, ajustamos el disponible
            harvest.setQuantityHarvested(totalHarvested);
            harvest.setAvailableQuantity(totalHarvested - quantityAlreadySold);

        } else if (!hasSales) {
            // Si no hay ventas, se puede cambiar libremente el total y el disponible se iguala
            harvest.setQuantityHarvested(totalHarvested);
            harvest.setAvailableQuantity(totalHarvested);
        }
        // Si tiene ventas y no se cambió el total, no hacemos nada con las cantidades


        // Actualizar otros campos
        harvest.setTypeHarvest(type);
        harvest.setDateHarvested(date);
        harvest.setDescription(description);
        harvest.setQuality(quality);
        harvest.setDestiny(destiny);
        harvest.setId_producer(codeProd);

        // Guardar cambios
        if (service.save(harvest)) {
            redirectAttributes.addFlashAttribute("mensaje", "Cosecha actualizada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la cosecha.");
        }

        return "redirect:/harvests/list"; // Siempre redirigir a la lista después del intento
    }


    @GetMapping("/list")
    public String listHarvests(Model model,
                               @RequestParam(value = "stateC", required = false) String quality,
                               @RequestParam(value = "destinyC", required = false) String destiny,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Harvest> harvestsPage;

        if (quality != null && !quality.isEmpty()) {
            harvestsPage = service.getHarvestByQualityPaged(quality, pageable);
            model.addAttribute("stateC", quality);
        } else if (destiny != null && !destiny.isEmpty()) {
            harvestsPage = service.getHarvestByDestinyPaged(destiny, pageable);
            model.addAttribute("destinyC", destiny);
        } else {
            harvestsPage = service.getAllPaged(pageable);
        }

        model.addAttribute("harvestList", harvestsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", harvestsPage.getTotalPages());
        model.addAttribute("totalItems", harvestsPage.getTotalElements());
        model.addAttribute("stateC", quality); // Pasar filtros para la paginación
        model.addAttribute("destinyC", destiny);
        model.addAttribute("activeModule", "harvests");
        model.addAttribute("activePage", "list");

        return "list_harvest";
    }


    @GetMapping("/page")
    public String paginate(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String stateC,
                           @RequestParam(required = false) String destinyC,
                           Model model) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Harvest> harvestsPage;

        if (stateC != null && !stateC.isEmpty()) {
            harvestsPage = service.getHarvestByQualityPaged(stateC, pageable);
        } else if (destinyC != null && !destinyC.isEmpty()) {
            harvestsPage = service.getHarvestByDestinyPaged(destinyC, pageable);
        } else {
            harvestsPage = service.getAllPaged(pageable);
        }

        model.addAttribute("harvestList", harvestsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", harvestsPage.getTotalPages());
        model.addAttribute("totalItems", harvestsPage.getTotalElements());
        model.addAttribute("stateC", stateC);
        model.addAttribute("destinyC", destinyC);

        return "harvest/table_harvest :: harvestListContent";
    }


    @PostMapping("/delete")
    public String deleteHarvest(@RequestParam("idHarvest") int idHarvest, RedirectAttributes redirectAttributes) {
        Harvest harvest = service.getById(idHarvest);
        if (harvest != null) {

            try {
                service.delete(harvest);
                redirectAttributes.addFlashAttribute("mensaje", "Cosecha y ventas asociadas (si existían) eliminadas exitosamente.");
            } catch (RuntimeException e) { // Captura errores del servicio (ej. DataAccessException)
                LOG.log(Level.SEVERE, "Error eliminando cosecha ID " + idHarvest, e);
                redirectAttributes.addFlashAttribute("error", "Error al eliminar la cosecha: " + e.getMessage());
            }
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
            @RequestParam("producerId") String codeProdStr,
            RedirectAttributes redirectAttributes) {

        if (!totalStr.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("error", "El campo 'Total' debe contener solo números enteros.");
            return "redirect:/harvests/form";
        }
        int totalHarvested;
        try {
            totalHarvested = Integer.parseInt(totalStr);
            if (totalHarvested <= 0) {
                redirectAttributes.addFlashAttribute("error", "El campo 'Total' debe ser mayor a cero.");
                return "redirect:/harvests/form";
            }
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "El valor de 'Total' no es un número entero válido.");
            return "redirect:/harvests/form";
        }


        if (date.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha no puede ser en el futuro.");
            return "redirect:/harvests/form";
        }

        if (type.length() > 50 || description.length() > 250 || destiny.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Verifique longitudes: Tipo (50), Descripción (250), Destino (100).");
            return "redirect:/harvests/form";
        }

        int codeProd;
        try {
            codeProd = Integer.parseInt(codeProdStr);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "ID de productor inválido.");
            return "redirect:/harvests/form";
        }
        // Validar que el productor exista
        if (producerService.getById(codeProd) == null) {
            redirectAttributes.addFlashAttribute("error", "El productor seleccionado no existe.");
            return "redirect:/harvests/form";
        }


        Harvest harvest = new Harvest();
        harvest.setTypeHarvest(type);
        harvest.setDateHarvested(date);
        harvest.setQuantityHarvested(totalHarvested);
        harvest.setAvailableQuantity(totalHarvested);
        harvest.setDescription(description);
        harvest.setQuality(quality);
        harvest.setDestiny(destiny);
        harvest.setRegisteredHarvest(true);
        harvest.setId_producer(codeProd);

        if (service.save(harvest)) {
            redirectAttributes.addFlashAttribute("mensaje", "Cosecha registrada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la cosecha.");
        }

        return "redirect:/harvests/form"; // Redirigir al formulario para poder registrar otra
    }
}