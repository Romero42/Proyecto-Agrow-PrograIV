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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private static final int PAGE_SIZE = 10; 

    @Autowired
    private SaleService saleService;

    @Autowired
    private Producer_Service producerService;

    @Autowired
    private HarvestService harvestService;

    
    @GetMapping("/select-producer")
    public String showSelectProducerForm(Model model,
                                         @RequestParam(required = false) Integer preselectedProducerId,
                                         @RequestParam(required = false) Integer preselectedHarvestId) {
        Pageable allProducersPageable = PageRequest.of(0, Integer.MAX_VALUE); 
        Page<Producer> producerPage = producerService.findAll(allProducersPageable);
        model.addAttribute("producers", producerPage.getContent());
        model.addAttribute("activeModule", "sales");
        model.addAttribute("preselectedProducerId", preselectedProducerId); 
        model.addAttribute("preselectedHarvestId", preselectedHarvestId);
        return "sales/select_producer";
    }

    
    @GetMapping("/form")
    public String showSaleForm(@RequestParam("producerId") int producerId,
                               @RequestParam(required = false) Integer preselectedHarvestId,
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
            
        }

        model.addAttribute("producer", producer);
        model.addAttribute("availableHarvests", availableHarvests);
        model.addAttribute("sale", new Sale()); 
        model.addAttribute("preselectedHarvestId", preselectedHarvestId); 
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
            
            return "redirect:/sales/select-producer";
        }

        
        if (buyerName.trim().isEmpty() || buyerPhone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nombre y teléfono del comprador son obligatorios.");
            
            return "redirect:/sales/form?producerId=" + harvest.getId_producer();
        }
        if (pricePerUnitSold <= 0) {
            redirectAttributes.addFlashAttribute("error", "El precio por unidad debe ser mayor a cero.");
            return "redirect:/sales/form?producerId=" + harvest.getId_producer();
        }


        Sale newSale = new Sale();
        newSale.setHarvest(harvest);
        newSale.setBuyerName(buyerName.trim());
        newSale.setBuyerPhone(buyerPhone.trim());
        newSale.setBuyerAddress(buyerAddress.trim());
        newSale.setTransportOption(transportOption.trim());
        newSale.setPricePerUnitSold(pricePerUnitSold);
        

        try {
            boolean success = saleService.processSale(newSale, quantitySold);
            if (success) {
                redirectAttributes.addFlashAttribute("mensaje", "Venta registrada exitosamente. Stock de cosecha actualizado.");
                return "redirect:/sales/list"; 
            } else {
                
                redirectAttributes.addFlashAttribute("error", "No se pudo registrar la venta (error inesperado).");
                return "redirect:/sales/form?producerId=" + harvest.getId_producer();
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar venta: " + e.getMessage());
            return "redirect:/sales/form?producerId=" + harvest.getId_producer() + "&preselectedHarvestId=" + harvestId; 
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error de base de datos al procesar la venta.");
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
        model.addAttribute("activePage", "list"); 
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
    @ResponseBody 
    public Harvest getHarvestDetails(@RequestParam("harvestId") int harvestId) {
        
        Harvest harvest = harvestService.getById(harvestId);
        
        
        return harvest; 
    }

}