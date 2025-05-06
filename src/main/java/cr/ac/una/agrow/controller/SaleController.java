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
    private Producer_Service producerService;

    @Autowired
    private HarvestService harvestService;


    @GetMapping("/select-producer")
    public String showSelectProducerForm(Model model,
                                         @RequestParam(required = false) Integer preselectedProducerId,
                                         @RequestParam(required = false) Integer preselectedHarvestId) {

        Pageable allProducersPageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Producer> producerPage = producerService.findAll(allProducersPageable);


        List<Producer> sellableProducers = producerPage.getContent().stream()
                .filter(producer -> {
                    List<Harvest> availableHarvests = harvestService.getAvailableHarvestsByProducer(producer.getId_producer());
                    return availableHarvests != null && !availableHarvests.isEmpty();
                })
                .collect(Collectors.toList());

        model.addAttribute("sellableProducers", sellableProducers);
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

            model.addAttribute("availableHarvests", new ArrayList<Harvest>());
        } else {
            model.addAttribute("availableHarvests", availableHarvests);
        }

        model.addAttribute("producer", producer);
        model.addAttribute("sale", new Sale());
        model.addAttribute("preselectedHarvestId", preselectedHarvestId);
        model.addAttribute("activeModule", "sales");
        model.addAttribute("activePage", "add");
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

        if (buyerName == null || buyerName.trim().isEmpty() || buyerPhone == null || buyerPhone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nombre y teléfono del comprador son obligatorios.");


            Harvest tempHarvest = harvestService.getById(harvestId);
            int producerId = (tempHarvest != null) ? tempHarvest.getId_producer() : 0;
            if (producerId == 0) return "redirect:/sales/select-producer";
            return "redirect:/sales/form?producerId=" + producerId + "&preselectedHarvestId=" + harvestId;
        }
        if (pricePerUnitSold <= 0) {
            redirectAttributes.addFlashAttribute("error", "El precio por unidad debe ser mayor a cero.");
            Harvest tempHarvest = harvestService.getById(harvestId);
            int producerId = (tempHarvest != null) ? tempHarvest.getId_producer() : 0;
            if (producerId == 0) return "redirect:/sales/select-producer";
            return "redirect:/sales/form?producerId=" + producerId + "&preselectedHarvestId=" + harvestId;
        }


        Sale newSale = new Sale();
        Harvest harvestRef = new Harvest();
        harvestRef.setIdHarvest(harvestId);
        newSale.setHarvest(harvestRef);

        newSale.setBuyerName(buyerName.trim());
        newSale.setBuyerPhone(buyerPhone.trim());
        newSale.setBuyerAddress(buyerAddress != null ? buyerAddress.trim() : null);
        newSale.setTransportOption(transportOption != null ? transportOption.trim() : null);
        newSale.setPricePerUnitSold(pricePerUnitSold);

        try {
            boolean success = saleService.processSale(newSale, quantitySold);
            if (success) {
                redirectAttributes.addFlashAttribute("mensaje", "Venta registrada exitosamente. Stock de cosecha actualizado.");
                return "redirect:/sales/list";
            } else {

                redirectAttributes.addFlashAttribute("error", "No se pudo registrar la venta (error inesperado).");
                Harvest tempHarvest = harvestService.getById(harvestId);
                int producerId = (tempHarvest != null) ? tempHarvest.getId_producer() : 0;
                if (producerId == 0) return "redirect:/sales/select-producer";
                return "redirect:/sales/form?producerId=" + producerId + "&preselectedHarvestId=" + harvestId;
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar venta: " + e.getMessage());
            Harvest tempHarvest = harvestService.getById(harvestId);
            int producerId = (tempHarvest != null) ? tempHarvest.getId_producer() : 0;
            if (producerId == 0) return "redirect:/sales/select-producer";
            return "redirect:/sales/form?producerId=" + producerId + "&preselectedHarvestId=" + harvestId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al procesar la venta. Intente de nuevo.");
            Harvest tempHarvest = harvestService.getById(harvestId);
            int producerId = (tempHarvest != null) ? tempHarvest.getId_producer() : 0;
            if (producerId == 0) return "redirect:/sales/select-producer";
            return "redirect:/sales/form?producerId=" + producerId;
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

    @GetMapping("/print-receipt")
    public String showPrintReceipt(@RequestParam("idSale") int idSale, Model model, RedirectAttributes redirectAttributes) {
        Optional<Sale> saleOpt = saleService.getSaleById(idSale);
        if (saleOpt.isPresent()) {
            Sale sale = saleOpt.get();

            model.addAttribute("sale", sale);

            return "sales/print_receipt";
        } else {
            redirectAttributes.addFlashAttribute("error", "Venta con ID " + idSale + " no encontrada para imprimir.");
            return "redirect:/sales/list";
        }
    }

    @GetMapping("/edit")
    public String showEditSaleForm(@RequestParam("idSale") int idSale, Model model, RedirectAttributes redirectAttributes) {
        Optional<Sale> saleOpt = saleService.getSaleById(idSale);
        if (saleOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Venta con ID " + idSale + " no encontrada para editar.");
            return "redirect:/sales/list";
        }
        Sale sale = saleOpt.get();
        Harvest harvest = sale.getHarvest();

        if (harvest == null) {
            redirectAttributes.addFlashAttribute("error", "Error: La cosecha asociada a esta venta ya no existe.");
            return "redirect:/sales/list";
        }


        int maxEditableQuantity = harvest.getAvailableQuantity() + sale.getQuantitySold();

        model.addAttribute("sale", sale);
        model.addAttribute("harvest", harvest);
        model.addAttribute("maxEditableQuantity", maxEditableQuantity);
        model.addAttribute("activeModule", "sales");
        return "sales/edit_sale";
    }

    @PostMapping("/update")
    public String updateSale(@RequestParam("idSale") int idSale,
                             @RequestParam("quantitySold") int newQuantitySold,
                             @RequestParam("buyerName") String buyerName,
                             @RequestParam("buyerPhone") String buyerPhone,
                             @RequestParam("buyerAddress") String buyerAddress,
                             @RequestParam("transportOption") String transportOption,
                             @RequestParam("pricePerUnitSold") double pricePerUnitSold,
                             RedirectAttributes redirectAttributes) {


        Sale updatedSaleData = new Sale();
        updatedSaleData.setIdSale(idSale);
        updatedSaleData.setBuyerName(buyerName);
        updatedSaleData.setBuyerPhone(buyerPhone);
        updatedSaleData.setBuyerAddress(buyerAddress);
        updatedSaleData.setTransportOption(transportOption);
        updatedSaleData.setPricePerUnitSold(pricePerUnitSold);


        try {
            boolean success = saleService.updateSale(updatedSaleData, newQuantitySold);
            if (success) {
                redirectAttributes.addFlashAttribute("mensaje", "Venta actualizada correctamente.");
                return "redirect:/sales/list";
            } else {

                redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la venta.");
                return "redirect:/sales/edit?idSale=" + idSale;
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar venta: " + e.getMessage());
            return "redirect:/sales/edit?idSale=" + idSale;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al actualizar la venta. Intente de nuevo.");
            return "redirect:/sales/edit?idSale=" + idSale;
        }
    }


    @PostMapping("/delete")
    public String deleteSale(@RequestParam("idSale") int idSale, RedirectAttributes redirectAttributes) {
        try {
            boolean success = saleService.deleteSale(idSale);
            if (success) {
                redirectAttributes.addFlashAttribute("mensaje", "Venta eliminada correctamente. Stock de cosecha restaurado.");
            } else {

                redirectAttributes.addFlashAttribute("error", "No se encontró la venta con ID " + idSale + " para eliminar.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al eliminar la venta. Verifique dependencias o intente de nuevo.");
        }
        return "redirect:/sales/list";
    }


    @GetMapping("/harvest-details")
    @ResponseBody
    public Harvest getHarvestDetails(@RequestParam("harvestId") int harvestId) {

        Harvest harvest = harvestService.getById(harvestId);
        return harvest;
    }

}