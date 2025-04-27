package cr.ac.una.agrow.controller.supply;

import cr.ac.una.agrow.domain.supplier.Supplier;
import cr.ac.una.agrow.domain.supply.Supply;
import cr.ac.una.agrow.data.supplier.DataSupplier;
import cr.ac.una.agrow.data.supply.DataSupply;
import cr.ac.una.agrow.util.Util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/supplies")
public class SupplyController {

    private static final DateTimeFormatter DATE_FORMAT_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;

    private void addSuppliersToModel(Model model) {
        List<Supplier> suppliers = DataSupplier.getAllSuppliers();
        model.addAttribute("suppliers", suppliers != null ? suppliers : Collections.emptyList());
    }

    @GetMapping("/list")
    public String listSupplies(
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "supplierId", required = false) Integer supplierId,
            Model model) {

        List<Supply> listaSupplies;
        String validationMessage = null;

        try {
            listaSupplies = DataSupply.getFilteredSupplies(searchTerm, category, supplierId);
            model.addAttribute("cantidad", listaSupplies.size());
            if (listaSupplies.isEmpty()) {
                validationMessage = (searchTerm != null || category != null || supplierId != null)
                        ? "No se encontraron suministros con los filtros aplicados."
                        : "No hay suministros registrados.";
            }
            if (supplierId != null && supplierId > 0) {
                Supplier filteredSupplier = DataSupplier.getSupplierById(supplierId);
                model.addAttribute("titulo", filteredSupplier != null
                        ? "Suministros del Proveedor: " + filteredSupplier.getCompanyName()
                        : "Suministros (Proveedor ID: " + supplierId + " no encontrado)");
            } else {
                model.addAttribute("titulo", "Listado de Suministros");
            }
        } catch (Exception e) {
            listaSupplies = Collections.emptyList();
            model.addAttribute("cantidad", 0);
            validationMessage = "Error al cargar la lista de suministros: " + e.getMessage();
            model.addAttribute("titulo", "Listado de Suministros");
        }

        addSuppliersToModel(model);
        model.addAttribute("supplies", listaSupplies);
        model.addAttribute("validate", validationMessage);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSupplierId", supplierId);
        model.addAttribute("activeModule", "supply");
        model.addAttribute("activePage", "list");
        return "list_supply";
    }

    @GetMapping("/form")
    public String showSupplyForm(@RequestParam(value = "supplierId", required = false) Integer supplierId, Model model) {
        addSuppliersToModel(model);
        Supply supply = new Supply();
        if (supplierId != null && supplierId > 0 && DataSupplier.getSupplierById(supplierId) != null) {
            supply.setSupplierId(supplierId);
        } else if (supplierId != null) {
            model.addAttribute("error_supplier", "El proveedor con ID " + supplierId + " no existe.");
        }
        model.addAttribute("supply", supply);
        model.addAttribute("isEdit", false);
        model.addAttribute("activeModule", "supply");
        model.addAttribute("activePage", "add");
        return "form_supply";
    }

    @PostMapping("/save")
    public String saveSupply(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("stock") String stockStr,
            @RequestParam("stockMinimo") String stockMinimoStr,
            @RequestParam("unitType") String unitType,
            @RequestParam("pricePerUnit") String pricePerUnitStr,
            @RequestParam("expirationDate") String expirationDateStr,
            @RequestParam("supplierId") String supplierIdStr,
            RedirectAttributes redirectAttributes) {

        String error = validateSupplyFields(name, category, stockStr, stockMinimoStr, unitType, pricePerUnitStr, expirationDateStr, supplierIdStr);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            Supply supplyData = createSupplyFromStrings(name, category, stockStr, stockMinimoStr, unitType, pricePerUnitStr, null, supplierIdStr);
            redirectAttributes.addFlashAttribute("supply", supplyData);
            redirectAttributes.addFlashAttribute("expirationDateStr", expirationDateStr);
            return "redirect:/supplies/form" + (supplyData.getSupplierId() != null ? "?supplierId=" + supplyData.getSupplierId() : "");
        }

        double stock = Double.parseDouble(stockStr);
        double stockMinimo = Double.parseDouble(stockMinimoStr);
        double pricePerUnit = Double.parseDouble(pricePerUnitStr);
        int supplierId = Integer.parseInt(supplierIdStr);
        LocalDate expDate;
        try {
            expDate = LocalDate.parse(expirationDateStr, DATE_FORMAT_INPUT);
            if (expDate.isBefore(LocalDate.now())) {
                throw new DateTimeParseException("", expirationDateStr, 0);
            }
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Fecha de vencimiento inválida o pasada. Use el selector.");
            Supply supplyData = createSupplyFromStrings(name, category, stockStr, stockMinimoStr, unitType, pricePerUnitStr, null, supplierIdStr);
            redirectAttributes.addFlashAttribute("supply", supplyData);
            redirectAttributes.addFlashAttribute("expirationDateStr", expirationDateStr);
            return "redirect:/supplies/form" + (supplierId > 0 ? "?supplierId=" + supplierId : "");
        }

        if (DataSupplier.getSupplierById(supplierId) == null) {
            redirectAttributes.addFlashAttribute("error", "El ID de proveedor seleccionado (" + supplierId + ") no existe.");
            Supply supplyData = createSupplyFromStrings(name, category, stockStr, stockMinimoStr, unitType, pricePerUnitStr, expDate, supplierIdStr);
            redirectAttributes.addFlashAttribute("supply", supplyData);
            redirectAttributes.addFlashAttribute("expirationDateStr", expirationDateStr);
            return "redirect:/supplies/form" + (supplierId > 0 ? "?supplierId=" + supplierId : "");
        }

        Supply supply = new Supply(0, name.trim(), category.trim(), stock, stockMinimo, unitType.trim(), pricePerUnit, expDate, supplierId);

        String resultMessage = DataSupply.saveSupply(supply);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
            return "redirect:/supplies/list";
        } else {
            redirectAttributes.addFlashAttribute("error", message);
            redirectAttributes.addFlashAttribute("supply", supply);
            redirectAttributes.addFlashAttribute("expirationDateStr", expirationDateStr);
            return "redirect:/supplies/form" + (supplierId > 0 ? "?supplierId=" + supplierId : "");
        }
    }

    @GetMapping("/edit")
    public String editSupply(@RequestParam("idSupply") int idSupply, Model model, RedirectAttributes redirectAttributes) {
        Supply supply = DataSupply.getSupply(idSupply);
        if (supply == null) {
            redirectAttributes.addFlashAttribute("error", "El suministro seleccionado (ID: " + idSupply + ") no existe.");
            return "redirect:/supplies/list";
        }
        addSuppliersToModel(model);
        model.addAttribute("supply", supply);
        model.addAttribute("isEdit", true);
        model.addAttribute("activeModule", "supply");
        return "edit_supply";
    }

    @PostMapping("/update")
    public String updateSupply(
            @RequestParam("idSupply") int idSupply,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("stock") String stockStr,
            @RequestParam("stockMinimo") String stockMinimoStr,
            @RequestParam("unitType") String unitType,
            @RequestParam("pricePerUnit") String pricePerUnitStr,
            @RequestParam("expirationDate") String expirationDateStr,
            @RequestParam("supplierId") String supplierIdStr,
            RedirectAttributes redirectAttributes) {

        String error = validateSupplyFields(name, category, stockStr, stockMinimoStr, unitType, pricePerUnitStr, expirationDateStr, supplierIdStr);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/supplies/edit?idSupply=" + idSupply;
        }

        double stock = Double.parseDouble(stockStr);
        double stockMinimo = Double.parseDouble(stockMinimoStr);
        double pricePerUnit = Double.parseDouble(pricePerUnitStr);
        int supplierId = Integer.parseInt(supplierIdStr);
        LocalDate expDate;
        try {
            expDate = LocalDate.parse(expirationDateStr, DATE_FORMAT_INPUT);
            if (expDate.isBefore(LocalDate.now())) {
                throw new DateTimeParseException("", expirationDateStr, 0);
            }
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Fecha de vencimiento inválida o pasada. Use el selector.");
            return "redirect:/supplies/edit?idSupply=" + idSupply;
        }

        if (DataSupplier.getSupplierById(supplierId) == null) {
            redirectAttributes.addFlashAttribute("error", "El ID de proveedor seleccionado (" + supplierId + ") no existe.");
            return "redirect:/supplies/edit?idSupply=" + idSupply;
        }

        Supply supply = new Supply(idSupply, name.trim(), category.trim(), stock, stockMinimo, unitType.trim(), pricePerUnit, expDate, supplierId);

        String resultMessage = DataSupply.updateSupply(supply);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
            return "redirect:/supplies/list";
        } else {
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/supplies/edit?idSupply=" + idSupply;
        }
    }

    @PostMapping("/delete")
    public String deleteSupply(@RequestParam("idSupply") int idSupply, RedirectAttributes redirectAttributes) {
        String resultMessage = DataSupply.deleteSupply(idSupply);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
        } else {
            redirectAttributes.addFlashAttribute("error", message);
        }
        return "redirect:/supplies/list";
    }

    @GetMapping("/view")
    public String viewSupply(@RequestParam("idSupply") int idSupply, Model model, RedirectAttributes redirectAttributes) {
        Supply supply = DataSupply.getSupply(idSupply);
        if (supply == null) {
            redirectAttributes.addFlashAttribute("error", "El suministro solicitado (ID: " + idSupply + ") no se encuentra.");
            return "redirect:/supplies/list";
        }
        Supplier supplier = DataSupplier.getSupplierById(supply.getSupplierId());
        model.addAttribute("supplierName", supplier != null ? supplier.getCompanyName() : "Desconocido");
        model.addAttribute("supply", supply);
        model.addAttribute("activeModule", "supply");
        return "view_supply";
    }

    private String validateSupplyFields(String name, String category, String stockStr, String stockMinimoStr,
                                        String unitType, String pricePerUnitStr, String expirationDateStr, String supplierIdStr) {
        if (name == null || name.trim().isEmpty()) return "El nombre es obligatorio.";
        if (category == null || category.trim().isEmpty()) return "La categoría es obligatoria.";
        if (unitType == null || unitType.trim().isEmpty()) return "El tipo de unidad es obligatorio.";
        if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) return "Debe seleccionar un proveedor.";

        try { if (Integer.parseInt(supplierIdStr) <= 0) return "ID de proveedor inválido."; } catch (NumberFormatException e) { return "ID de proveedor debe ser un número."; }
        try { if (Double.parseDouble(stockStr) < 0) return "El stock debe ser un número no negativo."; } catch (NumberFormatException e) { return "El stock debe ser un número válido."; }
        try { if (Double.parseDouble(stockMinimoStr) < 0) return "El stock mínimo debe ser un número no negativo."; } catch (NumberFormatException e) { return "El stock mínimo debe ser un número válido."; }
        try { if (Double.parseDouble(pricePerUnitStr) <= 0) return "El precio por unidad debe ser mayor que cero."; } catch (NumberFormatException e) { return "El precio por unidad debe ser un número válido."; }
        if (expirationDateStr == null || expirationDateStr.trim().isEmpty()) return "La fecha de vencimiento es obligatoria.";
        try { LocalDate.parse(expirationDateStr, DATE_FORMAT_INPUT); } catch (DateTimeParseException e) { return "Formato de fecha de vencimiento inválido."; }

        return null;
    }

    private Supply createSupplyFromStrings(String name, String category, String stockStr, String stockMinimoStr, String unitType, String pricePerUnitStr, LocalDate expDate, String supplierIdStr) {
        double stock = 0.0, stockMin = 0.0, price = 0.0;
        int supId = 0;
        try { stock = Double.parseDouble(stockStr); } catch (NumberFormatException ignored) {}
        try { stockMin = Double.parseDouble(stockMinimoStr); } catch (NumberFormatException ignored) {}
        try { price = Double.parseDouble(pricePerUnitStr); } catch (NumberFormatException ignored) {}
        try { supId = Integer.parseInt(supplierIdStr); } catch (NumberFormatException ignored) {}
        return new Supply(0, name, category, stock, stockMin, unitType, price, expDate, supId);
    }
}
