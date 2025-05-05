package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.Transport;
import cr.ac.una.agrow.service.Transport_Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transport")
public class Transport_Controller {

    @Autowired
    private Transport_Service service;

    @GetMapping("/list")
    public String listadoTransportes(Model model) {
        return filtrarTransportes(model, null, null, 0);
    }

    @GetMapping("/filtrar")
    public String filtrarTransportes(
            Model model,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String destino,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<Transport> transportsPage;

        // Lógica de filtrado mejorada
        try {
            if (estado != null && destino != null && !destino.isEmpty()) {
                transportsPage = service.findByEstadoAndDestination(
                        Boolean.parseBoolean(estado),
                        destino.trim(),
                        pageable
                );
            } else if (estado != null) {
                transportsPage = service.findByEstado(
                        Boolean.parseBoolean(estado),
                        pageable
                );
            } else if (destino != null && !destino.isEmpty()) {
                transportsPage = service.findByDestination(
                        destino.trim(),
                        pageable
                );
            } else {
                transportsPage = service.getAllPaginated(pageable);
            }
        } catch (Exception e) {
            transportsPage = Page.empty(pageable);
            model.addAttribute("error", "Error al filtrar los transportes");
        }

        // Preparar modelo
        model.addAttribute("transportes", transportsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transportsPage.getTotalPages());
        model.addAttribute("estadoFiltro", estado != null ? Boolean.parseBoolean(estado) : null);
        model.addAttribute("destinoBuscado", destino);
        model.addAttribute("cantidad", transportsPage.getTotalElements());

        return isAjaxRequest() ? "transport/transport_table :: contenido" : "transport/list_transport";
    }

// Método auxiliar para detectar peticiones AJAX
    private boolean isAjaxRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        }
        return false;
    }

    @GetMapping("/pageCurrent")
    public String pageCurrent(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Transport> transportsPage = service.getAllPaginated(pageable);

        model.addAttribute("validate", transportsPage.isEmpty() ? "No se encontraron transportes registrados" : null);
        model.addAttribute("useFilter", false);
        model.addAttribute("pag", true);
        model.addAttribute("activeModule", "transport");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transportsPage.getTotalPages());
        model.addAttribute("transportes", transportsPage.getContent());

        return "transport/list_transport";
    }

    @PostMapping("/guardar-transporte")
    public String saveTransport(RedirectAttributes redirectAttributes,
            @RequestParam(required = false) Integer transportId, // Cambiado a Integer y no requerido
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
            // Solo establecer ID si se proporciona (para actualizaciones)
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

            if (service.save(transport)) {
                redirectAttributes.addFlashAttribute("mensaje", "Transporte " + (transportId != null ? "actualizado" : "registrado") + " exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo guardar el transporte");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar transporte: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/transport/list?reload=true";
    }

    @GetMapping("/formulario-transporte")
    public String saveView(Model model) {
        model.addAttribute("activeModule", "transport");
        return "transport/form_transport";
    }

    @PostMapping("/eliminar-transporte")
    public String deleteTransport(@RequestParam int transportId, RedirectAttributes redirectAttributes) {
        if (service.deleteById(transportId)) {
            redirectAttributes.addFlashAttribute("mensaje", "Transporte eliminado exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el transporte");
        }
        return "redirect:/transport/list";
    }

    @GetMapping("/detalles-transporte")
    public String view(@RequestParam int id, Model model) {
        model.addAttribute("activeModule", "transport");
        model.addAttribute("transporte", service.getById(id));
        return "transport/detail_transport";
    }

    @GetMapping("/actualizar-transporte")
    public String editTransport(RedirectAttributes redirectAttributes,
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

        Transport transport = new Transport(
                transportId, clientId, compraId, estado,
                transportOrigin, transportDestination,
                LocalDate.parse(transportDate),
                LocalTime.parse(departureTime),
                LocalTime.parse(arrivalTime),
                transportCost
        );

        if (service.update(transport)) {
            redirectAttributes.addFlashAttribute("mensaje", "Transporte actualizado exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el transporte");
        }

        return "redirect:/transport/list";
    }

    @GetMapping("/editar-transporte")
    public String editView(Model model, @RequestParam int id) {
        model.addAttribute("transporte", service.getById(id));
        model.addAttribute("activeModule", "transport");
        return "transport/edit_transport";
    }

    @GetMapping("/buscarTransportePorDestino")
    public String buscarPorDestino(Model model,
            @RequestParam String destino,
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Transport> transportsPage = service.findByDestination(destino, pageable);
        return configurarModeloFiltrado(model, transportsPage, page, "destino", destino);
    }

    @GetMapping("/buscarTransportePorEstado")
    public String buscarPorEstado(Model model,
            @RequestParam boolean estado,
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Transport> transportsPage = service.findByEstado(estado, pageable);
        return configurarModeloFiltrado(model, transportsPage, page, "estado", estado);
    }

    @GetMapping("/lista-transportes")
    public String mostrarTodos(Model model,
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Transport> transportsPage = service.getAllPaginated(pageable);
        return configurarModeloFiltrado(model, transportsPage, page, null, null);
    }

    private String configurarModeloFiltrado(Model model, Page<Transport> page, int currentPage, String filtroTipo, Object filtroValor) {
        model.addAttribute("validate", page.isEmpty() ? "No se encontraron transportes con los filtros aplicados" : null);
        model.addAttribute("activeModule", "transport");
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("transportes", page.getContent());
        model.addAttribute("useFilter", true);
        model.addAttribute("pag", true);

        if ("destino".equals(filtroTipo)) {
            model.addAttribute("destinoBuscado", filtroValor);
        } else if ("estado".equals(filtroTipo)) {
            model.addAttribute("estadoFiltro", filtroValor);
        }

        return "transport/list_transport"; // <-- CORREGIDO: antes devolvía "table_transport"
    }
}
