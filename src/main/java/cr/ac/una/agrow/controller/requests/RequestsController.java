package cr.ac.una.agrow.controller.requests;

import cr.ac.una.agrow.domain.requests.Requests;
import cr.ac.una.agrow.service.requests.RequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestsController {

    @Autowired
    private RequestsService requestService;

    // Prepara la lista de solicitudes y el mensaje de validación
    private void prepareModelForList(Model model, String search) {
        List<Requests> requests;
        String validateMsg = null;

        if (search != null && !search.trim().isEmpty()) {
            requests = requestService.searchName(search.trim());
            if (requests.isEmpty()) {
                validateMsg = "No se encontraron solicitudes con: '" + search.trim() + "'";
            }
            model.addAttribute("searchTerm", search.trim());
        } else {
            requests = requestService.getAll();
            if (requests.isEmpty()) {
                validateMsg = "No hay solicitudes registradas.";
            }
        }

        model.addAttribute("listR", requests);
        model.addAttribute("validate", validateMsg);
    }

    @GetMapping("/list")
    public String listRequests(@RequestParam(required = false) String search, Model model) {
        prepareModelForList(model, search);
        model.addAttribute("activeModule", "requests");
        model.addAttribute("activePage", "list");
        return "requests/requests_list";
    }

    @GetMapping("/table")
    public String getRequestsTable(@RequestParam(required = false) String search, Model model) {
        prepareModelForList(model, search);
        return "requests/table_requests :: requestTableContent";
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("request", new Requests());
        model.addAttribute("activeModule", "requests");
        model.addAttribute("activePage", "add");
        return "requests/form_requests";
    }

    @PostMapping("/save")
    public String saveRequest(@Validated @ModelAttribute("request") Requests request,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("activeModule", "requests");
            model.addAttribute("activePage", "add");
            return "requests/form_requests";
        }

        requestService.save(request);
        redirectAttributes.addFlashAttribute("mensaje", "Solicitud guardada correctamente");
        return "redirect:/requests/list";
    }

    @GetMapping("/view")
    public String viewRequest(@RequestParam int id, Model model, RedirectAttributes redirectAttributes) {
        Requests req = requestService.getById(id);
        if (req != null) {
            model.addAttribute("requests", req);
            model.addAttribute("activeModule", "requests");
            return "requests/view_requests";
        } else {
            redirectAttributes.addFlashAttribute("error", "La solicitud no existe");
            return "redirect:/requests/list";
        }
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam int id, Model model, RedirectAttributes redirectAttributes) {
        Requests req = requestService.getById(id);
        if (req != null) {
            model.addAttribute("requests", req);
            model.addAttribute("activeModule", "requests");
            model.addAttribute("activePage", "edit");
            return "requests/update_requests";
        } else {
            redirectAttributes.addFlashAttribute("error", "La solicitud no existe");
            return "redirect:/requests/list";
        }
    }

    @PostMapping("/delete")
    public String deleteRequest(@RequestParam int id, RedirectAttributes redirectAttributes) {
        try {
            requestService.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la solicitud. Verifique dependencias.");
        }
        return "redirect:/requests/list";
    }
}
