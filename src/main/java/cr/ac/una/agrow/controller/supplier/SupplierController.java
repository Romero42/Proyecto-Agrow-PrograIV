package cr.ac.una.agrow.controller.supplier;

import cr.ac.una.agrow.domain.supplier.Supplier;
import cr.ac.una.agrow.service.supplier.Supplier_Service; // Importa Supplier_Service
import cr.ac.una.agrow.util.Util;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Autowired
    private Supplier_Service supplierService;

    private static final DateTimeFormatter DATE_FORMAT_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int PAGE_SIZE = 5;
    private void prepareModelListPaginated(Model model, String search, int page) {
        Page<Supplier> supplierPage;
        String validationMessage = null;

        if (search != null && !search.trim().isEmpty()) {
            supplierPage = supplierService.searchPagination(search.trim(), page, PAGE_SIZE);
            if (supplierPage.isEmpty()) {
                validationMessage = "No se encontraron proveedores con el criterio: '" + search + "'";
            }
            model.addAttribute("search", search.trim());
        } else {
            supplierPage = supplierService.getAllPaginated(page, PAGE_SIZE);
            if (supplierPage.isEmpty() && page ==0) {
                validationMessage = "No hay proveedores registrados.";
            } else if (supplierPage.isEmpty()) {
                validationMessage = "La página está vacía";
            }
        }
        model.addAttribute("listS", supplierPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", supplierPage.getTotalPages());
        model.addAttribute("totalItems", supplierPage.getTotalElements());
        model.addAttribute("validate", validationMessage);

    }

    private void prepareModelForList(Model model, String search) {
        List<Supplier> suppliers;
        String validationMessage = null;

        if (search != null && !search.trim().isEmpty()) {
            suppliers = supplierService.search(search.trim());
            if (suppliers.isEmpty()) {
                validationMessage = "No se encontraron proveedores con: '" + search.trim() + "'";
            }
            model.addAttribute("search", search.trim());
        } else {
            suppliers = supplierService.getAll();
            if (suppliers.isEmpty()) {
                validationMessage = "No hay proveedores registrados.";

            }

            model.addAttribute("listS", suppliers);
            model.addAttribute("validate", validationMessage);

        }
    }

    @GetMapping("/list")
    public String listSuppliers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

            prepareModelListPaginated(model, search, page);
        model.addAttribute("activeModule", "supplier");
        model.addAttribute("activePage", "list");
        return "supplier/supplier_list";
    }

    @GetMapping("/table")
    public String supplierTable(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        prepareModelListPaginated(model, search, page);
        return "supplier/supplier_table :: supplierTable";
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

        String error = Supplier_Service.validateSupplierData(supplierIdStr, supplierName, companyName, phoneNumberStr, email, registrationDateStr, creditLimitStr); // Usa Supplier_Service
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

        String resultMessage = String.valueOf(supplierService.save(supplier)); // Usa supplierService
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        boolean success = supplierService.save(supplier);
        if (success) {
            redirectAttributes.addFlashAttribute("mensaje", "Proveedor guardado exitosamente.");
            return "redirect:/suppliers/list";
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el proveedor.");
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

        Supplier supplier = supplierService.getById(id); // Usa supplierService
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

        String error = Supplier_Service.validateSupplierData(supplierIdStr, supplierName, companyName, phoneNumberStr, email, registrationDateStr, creditLimitStr); // Usa Supplier_Service
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


        boolean success = supplierService.save(supplier);
        if (success) {
            redirectAttributes.addFlashAttribute("mensaje", "Proveedor guardado exitosamente.");
           return "redirect:/suppliers/list";
        } else {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el proveedor.");
            redirectAttributes.addFlashAttribute("supplier", supplier);
            redirectAttributes.addFlashAttribute("registrationDateStr", registrationDateStr);
            return "redirect:/suppliers/edit?id=" + supplierIdentification;
        }
    }

    @PostMapping("/delete")
    public String deleteSupplier(
            @RequestParam("id") int id,
            RedirectAttributes redirectAttributes) {

        Supplier supplier = supplierService.getById(id); // Usa supplierService
        if (supplier == null) {
            redirectAttributes.addFlashAttribute("error", "Proveedor no encontrado con ID: " + id);
            return "redirect:/suppliers/list";
        }
        supplierService.delete(supplier); // Usa supplierService
        redirectAttributes.addFlashAttribute("mensaje", "Proveedor eliminado exitosamente.");
        return "redirect:/suppliers/list";
    }

    @GetMapping("/view")
    public String viewSupplier(
            @RequestParam("id") int id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Supplier supplier = supplierService.getById(id); // Usa supplierService
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
