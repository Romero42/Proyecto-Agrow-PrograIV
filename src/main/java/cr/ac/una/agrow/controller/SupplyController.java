package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.supplier.Supplier;
import cr.ac.una.agrow.domain.Supply;
import cr.ac.una.agrow.service.SupplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/supplies")
public class SupplyController {

    private static final int MAX_VISIBLE_PAGINATION_LINKS = 5;

    private static final DateTimeFormatter DATE_FORMAT_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int DEFAULT_PAGE_SIZE = 5;

    @Autowired
    private SupplyService supplyService;

    private void addSuppliersToModel(Model model) {
        List<Supplier> suppliers = supplyService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers != null ? suppliers : Collections.emptyList());
    }

    @GetMapping("/list")
    public String listSupplies(
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "supplierId", required = false) Integer supplierId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        Page<Supply> supplyPage;
        String validationMessage = null;
        int totalPages = 0;

        try {
            supplyPage = supplyService.getFilteredSuppliesPaginated(searchTerm, category, supplierId, pageable);
            totalPages = supplyPage.getTotalPages(); // Get total pages

            if (supplyPage.isEmpty()) {
                validationMessage = (searchTerm != null || category != null || supplierId != null)
                        ? "No se encontraron suministros con los filtros aplicados."
                        : "No hay suministros registrados.";
            }

            int startPage;
            int endPage;
            if (totalPages <= MAX_VISIBLE_PAGINATION_LINKS) {
                startPage = 0;
                endPage = totalPages > 0 ? totalPages - 1 : 0;
            } else {
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
                startPage = 0;
                endPage = -1;
            }

            model.addAttribute("supplies", supplyPage.getContent());
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", supplyPage.getTotalElements());
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);

            if (supplierId != null && supplierId > 0) {
                Optional<Supplier> filteredSupplierOpt = supplyService.findSupplierById(supplierId);
                model.addAttribute("titulo", filteredSupplierOpt.map(s -> "Suministros del Proveedor: " + s.getCompanyName()).orElse("Suministros (Proveedor ID: " + supplierId + " no encontrado)"));
            } else {
                model.addAttribute("titulo", "Listado de Suministros");
            }


        } catch (Exception e) {
            validationMessage = "Error al cargar la lista de suministros: " + e.getMessage();
            model.addAttribute("titulo", "Listado de Suministros");
            model.addAttribute("supplies", Collections.emptyList());
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalItems", 0L);
            model.addAttribute("startPage", 0);
            model.addAttribute("endPage", -1);
        }

        addSuppliersToModel(model);
        model.addAttribute("validate", validationMessage);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSupplierId", supplierId);
        model.addAttribute("activeModule", "supply");
        model.addAttribute("activePage", "list");

        return "supply/list_supply";
    }

    @GetMapping("/table")
    public String getSupplyTableFragment(
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "supplierId", required = false) Integer supplierId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        Page<Supply> supplyPage;
        String validationMessage = null;
        int totalPages = 0;

        try {
            supplyPage = supplyService.getFilteredSuppliesPaginated(searchTerm, category, supplierId, pageable);
            totalPages = supplyPage.getTotalPages();
            if (supplyPage.isEmpty()) {
                validationMessage = "No se encontraron suministros con los filtros aplicados.";
            }
        } catch (Exception e) {
            supplyPage = Page.empty(pageable);
            validationMessage = "Error al cargar los datos: " + e.getMessage();
            totalPages = 0;
        }

        int startPage;
        int endPage;
        if (totalPages <= MAX_VISIBLE_PAGINATION_LINKS) {
            startPage = 0;
            endPage = totalPages > 0 ? totalPages - 1 : 0;
        } else {
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
            startPage = 0;
            endPage = -1;
        }

        model.addAttribute("supplies", supplyPage.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", supplyPage.getTotalElements());
        model.addAttribute("startPage", startPage); // Pass range to fragment
        model.addAttribute("endPage", endPage);     // Pass range to fragment
        model.addAttribute("validate", validationMessage);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSupplierId", supplierId);

        return "supply/table_supply :: supplyTable";
    }

    @GetMapping("/form")
    public String showSupplyForm(@RequestParam(value = "supplierId", required = false) Integer supplierIdParam, Model model) {
        addSuppliersToModel(model);
        Supply supply = new Supply();
        if (supplierIdParam != null) {
            supplyService.findSupplierById(supplierIdParam).ifPresentOrElse(
                    supply::setSupplier,
                    () -> model.addAttribute("error_supplier", "El proveedor con ID " + supplierIdParam + " no existe.")
            );
        }
        model.addAttribute("supply", supply);
        model.addAttribute("activeModule", "supply");
        model.addAttribute("activePage", "add");
        return "supply/form_supply";
    }

    @PostMapping("/save")
    public String saveSupply(@ModelAttribute Supply supply,
                             @RequestParam("supplierId") Integer supplierId,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        Optional<Supplier> supplierOpt = supplyService.findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El ID de proveedor seleccionado (" + supplierId + ") no existe.");
            redirectAttributes.addFlashAttribute("supply", supply);
            return "redirect:/supplies/form";
        }
        supply.setSupplier(supplierOpt.get());

        String validationError = validateSupplyObject(supply);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("error", validationError);
            redirectAttributes.addFlashAttribute("supply", supply);
            return "redirect:/supplies/form?supplierId=" + supplierId;
        }

        supply.setName(supply.getName().trim());
        supply.setCategory(supply.getCategory().trim());
        supply.setUnitType(supply.getUnitType().trim());

        if (supplyService.saveSupply(supply)) {
            redirectAttributes.addFlashAttribute("mensaje", "Suministro '" + supply.getName() + "' guardado exitosamente.");
            return "redirect:/supplies/list";
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el suministro en la base de datos.");
            redirectAttributes.addFlashAttribute("supply", supply);
            return "redirect:/supplies/form?supplierId=" + supplierId;
        }
    }

    @GetMapping("/edit")
    public String editSupply(@RequestParam("idSupply") int idSupply, Model model, RedirectAttributes redirectAttributes) {
        Optional<Supply> supplyOpt = supplyService.getSupplyById(idSupply);
        if (supplyOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El suministro seleccionado (ID: " + idSupply + ") no existe.");
            return "redirect:/supplies/list";
        }
        addSuppliersToModel(model);
        model.addAttribute("supply", supplyOpt.get());
        model.addAttribute("activeModule", "supply");
        return "supply/edit_supply";
    }


    @PostMapping("/update")
    public String updateSupply(@ModelAttribute Supply supply,
                               @RequestParam("supplierId") Integer supplierId,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (supply.getIdSupply() <= 0 || supplyService.getSupplyById(supply.getIdSupply()).isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Error: Suministro con ID " + supply.getIdSupply() + " no encontrado para actualizar.");
            return "redirect:/supplies/list";
        }

        Optional<Supplier> supplierOpt = supplyService.findSupplierById(supplierId);
        if (supplierOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El ID de proveedor seleccionado (" + supplierId + ") no existe.");
            return "redirect:/supplies/edit?idSupply=" + supply.getIdSupply();
        }
        supply.setSupplier(supplierOpt.get());

        String validationError = validateSupplyObject(supply);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("error", validationError);
            return "redirect:/supplies/edit?idSupply=" + supply.getIdSupply();
        }

        supply.setName(supply.getName().trim());
        supply.setCategory(supply.getCategory().trim());
        supply.setUnitType(supply.getUnitType().trim());

        if (supplyService.saveSupply(supply)) {
            redirectAttributes.addFlashAttribute("mensaje", "Suministro '" + supply.getName() + "' actualizado exitosamente.");
            return "redirect:/supplies/list";
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el suministro en la base de datos.");
            return "redirect:/supplies/edit?idSupply=" + supply.getIdSupply();
        }
    }

    @PostMapping("/delete")
    public String deleteSupply(@RequestParam("idSupply") int idSupply, RedirectAttributes redirectAttributes) {
        Optional<Supply> supplyOpt = supplyService.getSupplyById(idSupply);
        String supplyName = supplyOpt.map(Supply::getName).orElse("ID " + idSupply);

        if (supplyService.deleteSupplyById(idSupply)) {
            redirectAttributes.addFlashAttribute("mensaje", "Suministro '" + supplyName + "' eliminado exitosamente.");
        } else {
            if (supplyOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar: Suministro con ID " + idSupply + " no encontrado.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el suministro '" + supplyName + "'. Puede estar en uso o hubo un error.");
            }
        }
        return "redirect:/supplies/list";
    }

    @GetMapping("/view")
    public String viewSupply(@RequestParam("idSupply") int idSupply, Model model, RedirectAttributes redirectAttributes) {
        Optional<Supply> supplyOpt = supplyService.getSupplyById(idSupply);
        if (supplyOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El suministro solicitado (ID: " + idSupply + ") no se encuentra.");
            return "redirect:/supplies/list";
        }
        model.addAttribute("supply", supplyOpt.get());
        model.addAttribute("activeModule", "supply");
        return "supply/view_supply";
    }

    private String validateSupplyObject(Supply supply) {
        if (supply.getName() == null || supply.getName().trim().isEmpty()) return "El nombre es obligatorio.";
        if (supply.getCategory() == null || supply.getCategory().trim().isEmpty()) return "La categoría es obligatoria.";
        if (supply.getUnitType() == null || supply.getUnitType().trim().isEmpty()) return "El tipo de unidad es obligatorio.";
        if (supply.getSupplier() == null || supply.getSupplier().getSupplierIdentification() <= 0) return "Debe seleccionar un proveedor válido.";

        if (supply.getStock() < 0) return "El stock debe ser un número no negativo.";
        if (supply.getStockMinimo() < 0) return "El stock mínimo debe ser un número no negativo.";
        if (supply.getPricePerUnit() <= 0) return "El precio por unidad debe ser mayor que cero.";

        if (supply.getExpirationDate() == null) return "La fecha de vencimiento es obligatoria.";
        if (supply.getExpirationDate().isBefore(LocalDate.now())) return "La fecha de vencimiento no puede ser pasada.";

        return null;
    }
}