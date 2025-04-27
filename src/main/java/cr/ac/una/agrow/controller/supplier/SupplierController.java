package cr.ac.una.agrow.controller.supplier;

import cr.ac.una.agrow.domain.supplier.Supplier;
import cr.ac.una.agrow.data.supplier.DataSupplier;
import cr.ac.una.agrow.service.supplier.SupplierService;
import cr.ac.una.agrow.util.Util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private static final DateTimeFormatter DATE_FORMAT_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;

    @GetMapping("/list")
    public String listSuppliers(
            @RequestParam(value = "search", required = false) String search,
            Model model) {

        List<Supplier> suppliers;
        String validationMessage = null;

        if (search != null && !search.trim().isEmpty()) {
            suppliers = DataSupplier.findSuppliers(search.trim());
            if (suppliers.isEmpty()) {
                validationMessage = "No se encontraron proveedores con el criterio: '" + search + "'";
            }
            model.addAttribute("searchTerm", search.trim());
        } else {
            suppliers = DataSupplier.getAllSuppliers();
            if (suppliers.isEmpty()) {
                validationMessage = "No hay proveedores registrados.";
            }
        }

        model.addAttribute("listS", suppliers);
        model.addAttribute("validate", validationMessage);
        model.addAttribute("activeModule", "supplier");
        model.addAttribute("activePage", "list");
        return "supplier/supplier_list";
    }

    @GetMapping("/form")
    public String showSaveForm(Model model) {
        Supplier supplier = new Supplier();
        supplier.setActive(true);
        model.addAttribute("supplier", supplier);
        model.addAttribute("isEdit", false);
        model.addAttribute("activeModule", "supplier");
        model.addAttribute("activePage", "add");
        return "supplier/form_supplier";
    }

    @PostMapping("/save")
    public String saveSupplier(
            @RequestParam("supplierIdentification") String supplierIdStr,
            @RequestParam("supplierName") String supplierName,
            @RequestParam("companyName") String companyName,
            @RequestParam("phoneNumber") String phoneNumberStr,
            @RequestParam("email") String email,
            @RequestParam("registrationDate") String registrationDateStr,
            @RequestParam(value = "isActive", defaultValue = "true") boolean isActive,
            @RequestParam("creditLimit") String creditLimitStr,
            RedirectAttributes redirectAttributes) {

        String error = SupplierService.validateSupplierData(supplierIdStr, supplierName, companyName, phoneNumberStr, email, registrationDateStr, creditLimitStr);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            Supplier supplierData = createSupplierFromStrings(supplierIdStr, supplierName, companyName, phoneNumberStr, email, null, isActive, creditLimitStr);
            redirectAttributes.addFlashAttribute("supplier", supplierData);
            redirectAttributes.addFlashAttribute("registrationDateStr", registrationDateStr);
            return "redirect:/suppliers/form";
        }

        int supplierIdentification = Integer.parseInt(supplierIdStr);
        int phoneNumber = Integer.parseInt(phoneNumberStr);
        double creditLimit = Double.parseDouble(creditLimitStr);
        LocalDate registrationDate;
        try {
            registrationDate = LocalDate.parse(registrationDateStr, DATE_FORMAT_INPUT);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Formato de fecha inválido. Use el selector.");
            Supplier supplierData = createSupplierFromStrings(supplierIdStr, supplierName, companyName, phoneNumberStr, email, null, isActive, creditLimitStr);
            redirectAttributes.addFlashAttribute("supplier", supplierData);
            redirectAttributes.addFlashAttribute("registrationDateStr", registrationDateStr);
            return "redirect:/suppliers/form";
        }

        Supplier supplier = new Supplier(supplierIdentification, supplierName.trim(), companyName.trim(), phoneNumber, email.trim(), registrationDate, isActive, creditLimit);

        String resultMessage = DataSupplier.saveSupplier(supplier);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
            return "redirect:/suppliers/list";
        } else {
            redirectAttributes.addFlashAttribute("error", message);
            redirectAttributes.addFlashAttribute("supplier", supplier);
            redirectAttributes.addFlashAttribute("registrationDateStr", registrationDateStr);
            return "redirect:/suppliers/form";
        }
    }

    @GetMapping("/edit")
    public String showUpdateForm(
            @RequestParam("id") int id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Supplier supplier = DataSupplier.getSupplierById(id);
        if (supplier == null) {
            redirectAttributes.addFlashAttribute("error", "Proveedor con ID " + id + " no encontrado.");
            return "redirect:/suppliers/list";
        }
        model.addAttribute("supplier", supplier);
        model.addAttribute("isEdit", true);
        model.addAttribute("activeModule", "supplier");
        return "supplier/update_supplier";
    }

    @PostMapping("/update")
    public String updateSupplier(
            @RequestParam("supplierIdentification") int supplierIdentification,
            @RequestParam("supplierName") String supplierName,
            @RequestParam("companyName") String companyName,
            @RequestParam("phoneNumber") String phoneNumberStr,
            @RequestParam("email") String email,
            @RequestParam("registrationDate") String registrationDateStr,
            @RequestParam(value = "isActive", defaultValue = "false") boolean isActive,
            @RequestParam("creditLimit") String creditLimitStr,
            RedirectAttributes redirectAttributes) {

        String supplierIdStr = String.valueOf(supplierIdentification);

        String error = SupplierService.validateSupplierData(supplierIdStr, supplierName, companyName, phoneNumberStr, email, registrationDateStr, creditLimitStr);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/suppliers/edit?id=" + supplierIdentification;
        }

        int phoneNumber = Integer.parseInt(phoneNumberStr);
        double creditLimit = Double.parseDouble(creditLimitStr);
        LocalDate registrationDate;
        try {
            registrationDate = LocalDate.parse(registrationDateStr, DATE_FORMAT_INPUT);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Formato de fecha inválido. Use el selector.");
            return "redirect:/suppliers/edit?id=" + supplierIdentification;
        }

        Supplier supplier = new Supplier(supplierIdentification, supplierName.trim(), companyName.trim(), phoneNumber, email.trim(), registrationDate, isActive, creditLimit);

        String resultMessage = DataSupplier.updateSupplier(supplier);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
        } else {
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/suppliers/edit?id=" + supplierIdentification;
        }
        return "redirect:/suppliers/list";
    }

    @PostMapping("/delete")
    public String deleteSupplier(
            @RequestParam("id") int id,
            RedirectAttributes redirectAttributes) {

        String resultMessage = DataSupplier.deleteSupplier(id);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
        } else {
            redirectAttributes.addFlashAttribute("error", message);
        }
        return "redirect:/suppliers/list";
    }

    @GetMapping("/view")
    public String viewSupplier(
            @RequestParam("id") int id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Supplier supplier = DataSupplier.getSupplierById(id);
        if (supplier == null) {
            redirectAttributes.addFlashAttribute("error", "Proveedor con ID " + id + " no encontrado.");
            return "redirect:/suppliers/list";
        }
        model.addAttribute("supplier", supplier);
        model.addAttribute("activeModule", "supplier");
        return "supplier/view_supplier";
    }

    private Supplier createSupplierFromStrings(String supplierIdStr, String supplierName, String companyName, String phoneNumberStr, String email, LocalDate regDate, boolean isActive, String creditLimitStr) {
        int id = 0;
        int phone = 0;
        double credit = 0.0;
        try { id = Integer.parseInt(supplierIdStr); } catch (NumberFormatException ignored) {}
        try { phone = Integer.parseInt(phoneNumberStr); } catch (NumberFormatException ignored) {}
        try { credit = Double.parseDouble(creditLimitStr); } catch (NumberFormatException ignored) {}
        return new Supplier(id, supplierName, companyName, phone, email, regDate, isActive, credit);
    }
}
