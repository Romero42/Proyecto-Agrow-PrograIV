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

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestsController {
    private static final DateTimeFormatter DATE_FORMAT_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private RequestsService requestService;

    @GetMapping("/list")
    public String listRequests(@RequestParam(required = false) String search, Model model) {
        List<Requests> requests;

        if (search != null && !search.isEmpty()) {
            requests = requestService.searchName(search);
            model.addAttribute("searchTerm", search);
        } else {
            requests = requestService.getAll();
        }

        if (requests.isEmpty()) {
            model.addAttribute("validate", "No se encontró solicitudes");
        }

        model.addAttribute("listR", requests);
        model.addAttribute("activeModule", "requests");
        return "requests/requests_list";
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("request", new Requests());
        model.addAttribute("activeModule", "requests");
        return "requests/form_requests";
    }

    @PostMapping("/save")
    public String saveRequest(@Validated @ModelAttribute("request") Requests request,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("activeModule", "requests");
            return "requests/form_requests";
        }

        requestService.save(request);
        redirectAttributes.addFlashAttribute("mensaje", "Solicitud guardada correctamente");
        return "redirect:/requests/list";
    }

    @GetMapping("/view")
    public String viewRequest(@RequestParam int id, Model model, RedirectAttributes redirectAttributes) {
        Requests requests = requestService.getById(id);

        if (requests != null) {
            model.addAttribute("requests", requests);
            model.addAttribute("activeModule", "requests");
            return "requests/view_requests";
        } else {
            redirectAttributes.addFlashAttribute("error", "La solicitud no existe");
            return "redirect:/requests/list";
        }
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam int id, Model model, RedirectAttributes redirectAttributes) {
        Requests requests = requestService.getById(id);

        if (requests != null) {
            model.addAttribute("requests", requests);
            model.addAttribute("activeModule", "requests");
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
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la solicitud");
        }
        return "redirect:/requests/list";
    }

}
