
package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.User;
import cr.ac.una.agrow.domain.Sale;
import cr.ac.una.agrow.domain.harvest.Harvest;
import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.service.HarvestService;
import cr.ac.una.agrow.service.Producer_Service;
import cr.ac.una.agrow.service.SaleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private static final int PAGE_SIZE = 10;
    private static final int MAX_VISIBLE_PAGINATION_LINKS = 5;
    private static final DateTimeFormatter DATE_FORMAT_DD_MM_YY = DateTimeFormatter.ofPattern("dd/MM/yy");

    @Autowired
    private SaleService saleService;

    @Autowired
    private Producer_Service producerService;

    @Autowired
    private HarvestService harvestService;

    private User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }

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

        Sale sale = new Sale();
        model.addAttribute("producer", producer);
        model.addAttribute("sale", sale);
        model.addAttribute("preselectedHarvestId", preselectedHarvestId);
        model.addAttribute("activeModule", "sales");
        model.addAttribute("activePage", "add");
        return "sales/form_sale";
    }

    @PostMapping("/process")
    public String processSale(@RequestParam("harvestId") int harvestId,
                              @RequestParam("quantitySold") int quantitySold,
                              @RequestParam("saleDate") String saleDateStr,
                              @RequestParam("buyerName") String buyerName,
                              @RequestParam("buyerPhone") String buyerPhone,
                              @RequestParam("buyerAddress") String buyerAddress,
                              @RequestParam("transportOption") String transportOption,
                              @RequestParam("pricePerUnitSold") double pricePerUnitSold,
                              RedirectAttributes redirectAttributes, HttpSession session) {

        User loggedInUser = getLoggedInUser(session);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para registrar una venta.");
            return "redirect:/login";
        }

        Harvest tempHarvestOnError = harvestService.getById(harvestId);
        int producerIdOnError = (tempHarvestOnError != null) ? tempHarvestOnError.getId_producer() : 0;
        String redirectOnErrorPath = (producerIdOnError == 0)
                ? "redirect:/sales/select-producer"
                : "redirect:/sales/form?producerId=" + producerIdOnError + "&preselectedHarvestId=" + harvestId;

        LocalDate saleDate;
        try {
            if (saleDateStr == null || saleDateStr.trim().isEmpty()) {
                throw new DateTimeParseException("La fecha de venta es obligatoria.", saleDateStr, 0);
            }
            saleDate = LocalDate.parse(saleDateStr, DATE_FORMAT_DD_MM_YY);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Formato de fecha de venta inválido. Use DD/MM/YY.");
            return redirectOnErrorPath;
        }
        if (saleDate.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha de venta no puede ser futura.");
            return redirectOnErrorPath;
        }
        if (buyerName == null || buyerName.trim().isEmpty() || buyerPhone == null || buyerPhone.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nombre y teléfono del comprador son obligatorios.");
            return redirectOnErrorPath;
        }
        if (pricePerUnitSold <= 0) {
            redirectAttributes.addFlashAttribute("error", "El precio por unidad debe ser mayor a cero.");
            return redirectOnErrorPath;
        }


        Sale newSale = new Sale();
        Harvest harvestRef = new Harvest();
        harvestRef.setIdHarvest(harvestId);
        newSale.setHarvest(harvestRef);

        newSale.setSaleDate(saleDate);
        newSale.setBuyerName(buyerName.trim());
        newSale.setBuyerPhone(buyerPhone.trim());
        newSale.setBuyerAddress(buyerAddress != null ? buyerAddress.trim() : null);
        newSale.setTransportOption(transportOption != null ? transportOption.trim() : null);
        newSale.setPricePerUnitSold(pricePerUnitSold);

        try {
            boolean success = saleService.processSale(newSale, quantitySold, loggedInUser);
            if (success) {
                redirectAttributes.addFlashAttribute("mensaje", "Venta registrada exitosamente. Stock de cosecha actualizado.");
                return "redirect:/sales/list";
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo registrar la venta (error inesperado).");
                return redirectOnErrorPath;
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar venta: " + e.getMessage());
            return redirectOnErrorPath;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al procesar la venta. Intente de nuevo.");
            return redirectOnErrorPath;
        }
    }

    @GetMapping({"/list", "/table"})
    public String listSales(Model model,
                            @RequestParam(value = "saleDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate saleDate,
                            @RequestParam(value = "buyerName", required = false) String buyerName,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                            HttpSession session) {

        User loggedInUser = getLoggedInUser(session);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Sale> salesPage = saleService.listFilteredSales(saleDate, buyerName, loggedInUser, pageable);

        int totalPages = salesPage.getTotalPages();
        int startPage = 0;
        int endPage = totalPages > 0 ? totalPages - 1 : 0;

        if (totalPages > MAX_VISIBLE_PAGINATION_LINKS) {
            int pagesToShowEachSide = MAX_VISIBLE_PAGINATION_LINKS / 2;
            startPage = Math.max(0, page - pagesToShowEachSide);
            endPage = Math.min(totalPages - 1, page + pagesToShowEachSide);
            if (page - startPage < pagesToShowEachSide) {
                endPage = Math.min(totalPages - 1, startPage + MAX_VISIBLE_PAGINATION_LINKS - 1);
            }
            if (endPage - page < pagesToShowEachSide) {
                startPage = Math.max(0, endPage - MAX_VISIBLE_PAGINATION_LINKS + 1);
            }
        }
        if (totalPages == 0) {
            startPage = 0; endPage = -1;
        }

        model.addAttribute("salesPage", salesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalItems", salesPage.getTotalElements());
        model.addAttribute("activeModule", "sales");
        model.addAttribute("activePage", "list");
        model.addAttribute("currentSaleDate", saleDate);
        model.addAttribute("currentBuyerName", buyerName);
        model.addAttribute("loggedInUser", loggedInUser); // Pasar usuario a la vista

        if ("XMLHttpRequest".equals(requestedWith)) {
            return "sales/table_sales.html :: salesTableContent";
        }
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
    public String showEditSaleForm(@RequestParam("idSale") int idSale, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        User loggedInUser = getLoggedInUser(session);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para editar.");
            return "redirect:/login";
        }

        Optional<Sale> saleOpt = saleService.getSaleById(idSale);
        if (saleOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Venta con ID " + idSale + " no encontrada para editar.");
            return "redirect:/sales/list";
        }
        Sale sale = saleOpt.get();

        // Comprobar propiedad si no es Admin
        if (!"Administrador".equals(loggedInUser.getType())) {
            if (sale.getCreatedByUser() == null || sale.getCreatedByUser().getId_User() != loggedInUser.getId_User()) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para editar esta venta.");
                return "redirect:/sales/list";
            }
        }

        Harvest harvest = sale.getHarvest();
        if (harvest == null) {
            Harvest freshHarvest = harvestService.getById(sale.getHarvest().getIdHarvest());
            if (freshHarvest == null) {
                redirectAttributes.addFlashAttribute("error", "Error: La cosecha asociada a esta venta ya no existe.");
                return "redirect:/sales/list";
            }
            sale.setHarvest(freshHarvest);
            harvest = freshHarvest;
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
                             @RequestParam("saleDate") String saleDateStr,
                             @RequestParam("buyerName") String buyerName,
                             @RequestParam("buyerPhone") String buyerPhone,
                             @RequestParam("buyerAddress") String buyerAddress,
                             @RequestParam("transportOption") String transportOption,
                             @RequestParam("pricePerUnitSold") double pricePerUnitSold,
                             RedirectAttributes redirectAttributes, HttpSession session) {

        User loggedInUser = getLoggedInUser(session);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para actualizar una venta.");
            return "redirect:/login";
        }

        LocalDate saleDate;
        try {
            if (saleDateStr == null || saleDateStr.trim().isEmpty()) {
                throw new DateTimeParseException("La fecha de venta es obligatoria.", saleDateStr, 0);
            }
            saleDate = LocalDate.parse(saleDateStr, DATE_FORMAT_DD_MM_YY);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Formato de fecha de venta inválido. Use DD/MM/YY.");
            return "redirect:/sales/edit?idSale=" + idSale;
        }
        if (saleDate.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha de venta no puede ser futura.");
            return "redirect:/sales/edit?idSale=" + idSale;
        }

        Sale updatedSaleData = new Sale();
        updatedSaleData.setIdSale(idSale);
        updatedSaleData.setSaleDate(saleDate);
        updatedSaleData.setBuyerName(buyerName);
        updatedSaleData.setBuyerPhone(buyerPhone);
        updatedSaleData.setBuyerAddress(buyerAddress);
        updatedSaleData.setTransportOption(transportOption);
        updatedSaleData.setPricePerUnitSold(pricePerUnitSold);

        try {
            boolean success = saleService.updateSale(updatedSaleData, newQuantitySold, loggedInUser);
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
        } catch (SecurityException e) { // Capturar error de permisos
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/sales/list";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al actualizar la venta. Intente de nuevo.");
            return "redirect:/sales/edit?idSale=" + idSale;
        }
    }

    @PostMapping("/delete")
    public String deleteSale(@RequestParam("idSale") int idSale, RedirectAttributes redirectAttributes, HttpSession session) {
        User loggedInUser = getLoggedInUser(session);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión para eliminar una venta.");
            return "redirect:/login";
        }

        try {
            boolean success = saleService.deleteSale(idSale, loggedInUser);
            if (success) {
                redirectAttributes.addFlashAttribute("mensaje", "Venta eliminada correctamente. Stock de cosecha restaurado.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se encontró la venta con ID " + idSale + " para eliminar.");
            }
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al eliminar la venta. Verifique dependencias o intente de nuevo.");
        }
        return "redirect:/sales/list";
    }

    @GetMapping("/harvest-details")
    @ResponseBody
    public Harvest getHarvestDetails(@RequestParam("harvestId") int harvestId) {
        return harvestService.getById(harvestId);
    }
}
