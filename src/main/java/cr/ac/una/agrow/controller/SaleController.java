package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.Sale;
import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.service.HarvestService;
import cr.ac.una.agrow.service.Producer_Service;
import cr.ac.una.agrow.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private SaleService saleService;

    @Autowired
    private Producer_Service producerService; // Service with JPA methods

    @Autowired
    private HarvestService harvestService;


    @GetMapping("/select-producer")
    public String showSelectProducerForm(Model model,
                                         @RequestParam(required = false) Integer preselectedProducerId,
                                         @RequestParam(required = false) Integer preselectedHarvestId) {
        Pageable allProducersPageable = PageRequest.of(0, Integer.MAX_VALUE); // Get all producers
        Page<Producer> producerPage = producerService.findAll(allProducersPageable);

        // Filter producers: only those with at least one available harvest
        List<Producer> sellableProducers = producerPage.getContent().stream()
                .filter(producer -> {
                    List<Harvest> availableHarvests = harvestService.getAvailableHarvestsByProducer(producer.getId_producer());
                    return availableHarvests != null && !availableHarvests.isEmpty();
                })
                .collect(Collectors.toList());

        model.addAttribute("sellableProducers", sellableProducers); // Pass filtered list
        model.addAttribute("activeModule", "sales");
        model.addAttribute("preselectedProducerId", preselectedProducerId);
        model.addAttribute("preselectedHarvestId", preselectedHarvestId);
        return "sales/select_producer";
    }


    @GetMapping("/form")
    public String showSaleForm(@RequestParam("producerId") int producerId,
                               @RequestParam(required = false) Integer preselectedHarvestId, // Renamed to match link
                               Model model, RedirectAttributes redirectAttributes) {
        Producer producer = producerService.getById(producerId);
        if (producer == null) {
            redirectAttributes.addFlashAttribute("error", "Productor no encontrado.");
            return "redirect:/sales/select-producer";
        }

        List<Harvest> availableHarvests = harvestService.getAvailableHarvestsByProducer(producerId);
        if (availableHarvests.isEmpty()) {
            model.addAttribute("producer", producer);
            model.addAttribute("infoMessage", "Este productor no tiene cosechas con stock disponible para la venta.");
            // Allow viewing the form even if no harvests, but disable harvest selection
            model.addAttribute("availableHarvests", new ArrayList<Harvest>()); // Pass empty list explicitly
        } else {
            model.addAttribute("availableHarvests", availableHarvests);
        }

        model.addAttribute("producer", producer);
        model.addAttribute("sale", new Sale());
        model.addAttribute("preselectedHarvestId", preselectedHarvestId); // Pass preselected harvest ID
        model.addAttribute("activeModule", "sales");
        return "sales/form_sale";
    }


    @PostMapping("/process")
    public String processSale(@RequestParam("harvestId") int harvestId,
                              @RequestParam("quantitySold") int quantitySold,
                              @RequestParam("buyerName") String buyerName,
                              @RequestParam("buyerPhone") String buyerPhone,
                              @RequestParam("buyerAddress") String buyerAddress,
                              @RequestParam("transportOption") String transportOption,
                              @RequestParam("pricePerUnitSold") double pricePerUnitSold,
                              RedirectAttributes redirectAttributes) {

        Harvest harvest = harvestService.getById(harvestId);
        if (harvest == null) {
            redirectAttributes.addFlashAttribute("error", "La cosecha seleccionada ya no existe.");
            // Redirect back to producer selection as harvest context is lost
            return "redirect:/sales/select-producer";
        }

        // Basic validation
        if (buyerName == null || buyerName.trim().isEmpty() || buyerPhone == null || buyerPhone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nombre y teléfono del comprador son obligatorios.");
            // Redirect back to the form, preserving producer and selected harvest
            return "redirect:/sales/form?producerId=" + harvest.getId_producer() + "&preselectedHarvestId=" + harvestId;
        }
        if (pricePerUnitSold <= 0) {
            redirectAttributes.addFlashAttribute("error", "El precio por unidad debe ser mayor a cero.");
            return "redirect:/sales/form?producerId=" + harvest.getId_producer() + "&preselectedHarvestId=" + harvestId;
        }


        Sale newSale = new Sale();
        newSale.setHarvest(harvest); // Associate with the actual Harvest object
        newSale.setBuyerName(buyerName.trim());
        newSale.setBuyerPhone(buyerPhone.trim());
        newSale.setBuyerAddress(buyerAddress != null ? buyerAddress.trim() : null); // Handle potential null address
        newSale.setTransportOption(transportOption != null ? transportOption.trim() : null); // Handle potential null transport
        newSale.setPricePerUnitSold(pricePerUnitSold);
        // totalSaleAmount will be calculated in the service

        try {
            boolean success = saleService.processSale(newSale, quantitySold); // Pass the Sale object
            if (success) {
                redirectAttributes.addFlashAttribute("mensaje", "Venta registrada exitosamente. Stock de cosecha actualizado.");
                return "redirect:/sales/list"; // Redirect to sales list on success
            } else {
                // Generic error if service returns false without exception (should ideally not happen with exceptions)
                redirectAttributes.addFlashAttribute("error", "No se pudo registrar la venta (error inesperado).");
                return "redirect:/sales/form?producerId=" + harvest.getId_producer() + "&preselectedHarvestId=" + harvestId;
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar venta: " + e.getMessage());
            return "redirect:/sales/form?producerId=" + harvest.getId_producer() + "&preselectedHarvestId=" + harvestId; // Preserve selection on error
        } catch (RuntimeException e) { // Catch broader runtime exceptions like DataAccessException
            redirectAttributes.addFlashAttribute("error", "Error de base de datos al procesar la venta.");
            // Don't pass preselected harvest ID here as the DB state might be inconsistent
            return "redirect:/sales/form?producerId=" + harvest.getId_producer();
        }
    }


    @GetMapping("/list")
    public String listSales(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Sale> salesPage = saleService.listAllSales(pageable);

        model.addAttribute("salesPage", salesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", salesPage.getTotalPages());
        model.addAttribute("totalItems", salesPage.getTotalElements());
        model.addAttribute("activeModule", "sales");
        model.addAttribute("activePage", "list"); // Added for sidebar active state
        return "sales/list_sales";
    }


    @GetMapping("/view")
    public String viewSale(@RequestParam("idSale") int idSale, Model model, RedirectAttributes redirectAttributes) {
        Optional<Sale> saleOpt = saleService.getSaleById(idSale);
        if (saleOpt.isPresent()) {
            model.addAttribute("sale", saleOpt.get());
            model.addAttribute("activeModule", "sales");
            return "sales/view_sale";
        } else {
            redirectAttributes.addFlashAttribute("error", "Venta con ID " + idSale + " no encontrada.");
            return "redirect:/sales/list";
        }
    }


    @GetMapping("/harvest-details")
    @ResponseBody // Return data directly, not a view name
    public Harvest getHarvestDetails(@RequestParam("harvestId") int harvestId) {
        // Basic fetch, could be enhanced with DTO to avoid sending unnecessary data
        Harvest harvest = harvestService.getById(harvestId);
        // Return the harvest object (Spring Boot will convert to JSON)
        // Handle null case if needed, e.g., return ResponseEntity.notFound().build()
        return harvest; // Jackson will serialize this
    }

}