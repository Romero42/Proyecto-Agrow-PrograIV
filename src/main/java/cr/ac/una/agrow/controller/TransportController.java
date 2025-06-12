package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.Transport;
import cr.ac.una.agrow.service.TransportService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/transport")
public class TransportController {

    private static final Logger LOG = Logger.getLogger(TransportController.class.getName());
    private static final int PAGE_SIZE = 5;
    private static final int MAX_VISIBLE_PAGINATION_LINKS = 5;
    
    @Autowired
    private TransportService transportService;

    @GetMapping("/list")
    public String listTransports(
            @RequestParam(required = false) String transport_type,
            @RequestParam(required = false) Boolean delivered,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Transport> transportsPage = transportService.getFilteredTransportsPaged(transport_type, delivered, pageable);
        prepareModelForView(model, transportsPage, transport_type, delivered, page);
        model.addAttribute("activeModule", "transport");
        model.addAttribute("activePage", "list");
        return "transport/list";
    }

    @GetMapping("/table")
    public String getTransportTable(
            @RequestParam(required = false) String transport_type,
            @RequestParam(required = false) Boolean delivered,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<Transport> transportsPage = transportService.getFilteredTransportsPaged(transport_type, delivered, pageable);
        
        
        prepareModelForView(model, transportsPage, transport_type, delivered, page);
        return "transport/table_transport :: transportListContent";
    }

    private void prepareModelForView(Model model, Page<Transport> pageData,
            String transportType, Boolean delivered, int page) {
        int totalPages = pageData.getTotalPages();
        int start = Math.max(0, page - MAX_VISIBLE_PAGINATION_LINKS / 2);
        int end = Math.min(totalPages - 1, start + MAX_VISIBLE_PAGINATION_LINKS - 1);

        if (totalPages > MAX_VISIBLE_PAGINATION_LINKS && (end - start + 1) < MAX_VISIBLE_PAGINATION_LINKS) {
            if (start == 0) {
                end = Math.min(totalPages - 1, MAX_VISIBLE_PAGINATION_LINKS - 1);
            } else if (end == totalPages - 1) {
                start = Math.max(0, totalPages - MAX_VISIBLE_PAGINATION_LINKS);
            }
        }
        if (totalPages == 0) {
            start = 0;
            end = -1;
        }

        model.addAttribute("transportList", pageData.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", pageData.getTotalElements());
        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);
        model.addAttribute("transport_type", transportType);
        model.addAttribute("delivered", delivered);
        model.addAttribute("transportTypes", Arrays.asList("Camión de carga", "Furgón de carga", "Pickup de la empresa", "Barco"));
        
        if (pageData.isEmpty()) {
            String message = "No hay transportes registrados.";
            if (transportType != null || delivered != null) {
                message = "No se encontraron transportes con esos filtros.";
            }
            model.addAttribute("validate", message);
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("transport", new Transport());
        model.addAttribute("transportTypes", Arrays.asList("Camión de carga", "Furgón de carga", "Pickup de la empresa", "Barco"));
        return "transport/register";
    }

    @PostMapping("/register")
    public String registerTransport(
            @RequestParam("transportDate") String transportDateStr,
            @RequestParam("transportType") String transportType,
            @RequestParam("transportCompany") String transportCompany,
            @RequestParam("driverName") String driverName,
            @RequestParam(value = "driverPhone", required = false) String driverPhone,
            @RequestParam("vehiclePlate") String vehiclePlate,
            @RequestParam("transportCost") BigDecimal transportCost,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        try {
            
            Optional<Transport> existingTransport = transportService.findByVehiclePlate(vehiclePlate);
            if (existingTransport.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Error: La placa " + vehiclePlate + " ya está en uso");
                return "redirect:/transport/register";
            }

            LocalDate transportDate = LocalDate.parse(transportDateStr);

            Transport transport = new Transport();
            transport.setTransportDate(transportDate);
            transport.setTransportType(transportType);
            transport.setTransportCompany(transportCompany);
            transport.setDriverName(driverName);
            transport.setDriverPhone(driverPhone);
            transport.setVehiclePlate(vehiclePlate);
            transport.setTransportCost(transportCost);

          
            boolean delivered = "Entregado".equalsIgnoreCase(status);
            transport.setDelivered(delivered);

            transportService.saveTransport(transport);

            redirectAttributes.addFlashAttribute("mensaje", "Transporte registrado exitosamente");
            return "redirect:/transport/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar transporte: " + e.getMessage());
            return "redirect:/transport/register";
        }
    }

    @GetMapping("/editView")
    public String showEditForm(@RequestParam("idTransport") int idTransport, Model model) {
        try {
            Transport transport = transportService.getTransportById(idTransport)
                    .orElseThrow(() -> new RuntimeException("Transporte no encontrado"));

            model.addAttribute("transport", transport);
            model.addAttribute("transportTypes", Arrays.asList("Camión de carga", "Furgón de carga", "Pickup de la empresa", "Barco"));
            return "transport/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Transporte no encontrado con ID: " + idTransport);
            return "redirect:/transport/list";
        }
    }

    @PostMapping("/edit")
    public String editTransport(
            @RequestParam("idTransport") int idTransport,
            @RequestParam String transport_date,
            @RequestParam String transport_type,
            @RequestParam String transport_company,
            @RequestParam String driver_name,
            @RequestParam String vehicle_plate,
            @RequestParam BigDecimal transport_cost,
            @RequestParam String driver_phone,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        try {
            Transport transport = transportService.getTransportById(idTransport)
                    .orElseThrow(() -> new RuntimeException("Transporte no encontrado"));

            
            Optional<Transport> existingWithPlate = transportService.findByVehiclePlate(vehicle_plate);
            if (existingWithPlate.isPresent() && existingWithPlate.get().getIdTransport() != idTransport) {
                redirectAttributes.addFlashAttribute("error", "Error: La placa " + vehicle_plate + " ya está en uso");
                return "redirect:/transport/editView?idTransport=" + idTransport;
            }

            LocalDate date = LocalDate.parse(transport_date);

            // Asignación de valores
            transport.setTransportDate(date);
            transport.setTransportType(transport_type);
            transport.setTransportCompany(transport_company);
            transport.setDriverName(driver_name);
            transport.setDriverPhone(driver_phone);
            transport.setVehiclePlate(vehicle_plate);
            transport.setTransportCost(transport_cost);
            transport.setDelivered("Entregado".equalsIgnoreCase(status));

            transportService.updateTransport(transport);

            redirectAttributes.addFlashAttribute("mensaje", "Transporte actualizado exitosamente");
            return "redirect:/transport/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar transporte: " + e.getMessage());
            return "redirect:/transport/editView?idTransport=" + idTransport;
        }
    }

    @PostMapping("/delete")
    public String deleteTransport(
            @RequestParam("idTransport") int idTransport,
            RedirectAttributes redirectAttributes) {

        try {
            boolean deleted = transportService.deleteTransport(idTransport);
            if (deleted) {
                redirectAttributes.addFlashAttribute("mensaje", "Transporte eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Transporte no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar transporte: " + e.getMessage());
        }

        return "redirect:/transport/list";
    }

    @GetMapping("/view")
    public String viewTransport(@RequestParam("idTransport") int idTransport, Model model) {
        try {
            Optional<Transport> transportOpt = transportService.getTransportById(idTransport);

            if (transportOpt.isPresent()) {
                model.addAttribute("transporte", transportOpt.get());
                return "transport/view";
            } else {
                model.addAttribute("error", "Transporte no encontrado con ID: " + idTransport);
                return "redirect:/transport/list";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar transporte: " + e.getMessage());
            return "redirect:/transport/list";
        }
    }
}