package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.Transport;
import cr.ac.una.agrow.service.Transport_Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transport")
public class Transport_Controller {

    @Autowired
    private Transport_Service service;

    // ================ MÉTODOS PRINCIPALES ================ //
    @GetMapping("/list")
    public String listadoTransportes(Model model) {
        return filtrarTransportes(model, null, null, 0);
    }

    @GetMapping("/filtrar")
    public String filtrarTransportes(
            Model model,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) String destino,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<Transport> transportsPage;

        try {
            // Simplificamos la lógica usando solo el método findByFilters
            transportsPage = service.findByFilters(estado, destino, pageable);

            // Debug para verificar los parámetros
            System.out.println("Filtrando con - Estado: " + estado + ", Destino: " + destino + ", Page: " + page);
        } catch (Exception e) {
            transportsPage = Page.empty(pageable);
            model.addAttribute("error", "Error al filtrar: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("transportes", transportsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transportsPage.getTotalPages());
        model.addAttribute("estadoFiltro", estado);
        model.addAttribute("destinoBuscado", destino);
        model.addAttribute("cantidad", transportsPage.getTotalElements());

        return isAjaxRequest() ? "transport/transport_table :: contenido" : "transport/list_transport";
    }
    // ================ CRUD OPERATIONS ================ //

    @PostMapping("/guardar-transporte")
    public String saveTransport(
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) Integer transportId,
            @RequestParam int clientId,
            @RequestParam int compraId,
            @RequestParam boolean estado,
            @RequestParam String transportOrigin,
            @RequestParam String transportDestination,
            @RequestParam String transportDate,
            @RequestParam String departureTime,
            @RequestParam String arrivalTime,
            @RequestParam double transportCost) {

        try {
            Transport transport = new Transport();
            if (transportId != null) {
                transport.setTransportId(transportId);
            }

            transport.setClientId(clientId);
            transport.setCompraId(compraId);
            transport.setEstado(estado);
            transport.setTransportOrigin(transportOrigin);
            transport.setTransportDestination(transportDestination);
            transport.setTransportDate(LocalDate.parse(transportDate));
            transport.setDepartureTime(LocalTime.parse(departureTime));
            transport.setArrivalTime(LocalTime.parse(arrivalTime));
            transport.setTransportCost(transportCost);

            Transport savedTransport = service.save(transport);

            if (savedTransport != null) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "Transporte " + (transportId != null ? "actualizado" : "registrado") + " exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Operación falló");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/transport/list";
    }

    @PostMapping("/actualizar-transporte")
    public String updateTransport(
            RedirectAttributes redirectAttributes,
            @RequestParam int transportId,
            @RequestParam int clientId,
            @RequestParam int compraId,
            @RequestParam boolean estado,
            @RequestParam String transportOrigin,
            @RequestParam String transportDestination,
            @RequestParam String transportDate,
            @RequestParam String departureTime,
            @RequestParam String arrivalTime,
            @RequestParam double transportCost) {

        try {
            Transport transport = new Transport(
                    transportId, clientId, compraId, estado,
                    transportOrigin, transportDestination,
                    LocalDate.parse(transportDate),
                    LocalTime.parse(departureTime),
                    LocalTime.parse(arrivalTime),
                    transportCost
            );

            Transport updated = service.save(transport);
            if (updated != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Transporte actualizado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el transporte");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/transport/list";
    }

    @PostMapping("/eliminar-transporte")
    public String deleteTransport(
            @RequestParam int transportId,
            RedirectAttributes redirectAttributes) {

        try {
            if (service.deleteById(transportId)) {
                redirectAttributes.addFlashAttribute("mensaje", "Transporte eliminado");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo eliminar");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/transport/list";
    }

    // ================ VIEW HANDLERS ================ //
    @GetMapping("/formulario-transporte")
    public String showForm(Model model) {
        model.addAttribute("activeModule", "transport");
        return "transport/form_transport";
    }

    @GetMapping("/editar-transporte")
    public String showEditForm(@RequestParam int id, Model model) {
        Optional<Transport> transporte = service.getById(id);
        if (!transporte.isPresent()) {
            return "redirect:/transport/list";
        }
        model.addAttribute("transporte", transporte.get());
        model.addAttribute("activeModule", "transport");
        return "transport/edit_transport";
    }

    @GetMapping("/detalles-transporte")
    public String showDetails(@RequestParam int id, Model model) {
        Optional<Transport> transporte = service.getById(id);
        if (!transporte.isPresent()) {
            return "redirect:/transport/list";
        }
        model.addAttribute("transporte", transporte.get());
        model.addAttribute("activeModule", "transport");
        return "transport/detail_transport";
    }

    // ================ UTILITY METHODS ================ //
    private boolean isAjaxRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
