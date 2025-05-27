package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.Transport;
import cr.ac.una.agrow.service.TransportService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transport")
public class TransportController {

    @Autowired
    private TransportService transportService;

    @GetMapping("/list")
    public String listTransports(
            @RequestParam(required = false) String transport_type,
            @RequestParam(required = false) Boolean delivered,
            Model model) {

        List<Transport> transports;

        if (transport_type != null && delivered != null) {
            transports = transportService.getByTypeAndDelivered(transport_type, delivered);
        } else if (transport_type != null) {
            transports = transportService.getByType(transport_type);
        } else if (delivered != null) {
            transports = transportService.getByDelivered(delivered);
        } else {
            transports = transportService.getAllTransports();
        }

        model.addAttribute("transportList", transports);
        return "transport/list";
    }

    @GetMapping("/register")  // ← Nuevo método para mostrar el formulario
    public String showRegisterForm(Model model) {
        // Si necesitas inicializar algún dato en el formulario, hazlo aquí
        model.addAttribute("transport", new Transport());  // Objeto vacío para el formulario
        return "transport/register";  // Nombre de la plantilla Thymeleaf (register.html)
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
            @RequestParam("status") String status, // cambio importante aquí
            RedirectAttributes redirectAttributes) {
        try {
            LocalDate transportDate = LocalDate.parse(transportDateStr);

            Transport transport = new Transport();
            transport.setTransportDate(transportDate);
            transport.setTransportType(transportType);
            transport.setTransportCompany(transportCompany);
            transport.setDriverName(driverName);
            transport.setDriverPhone(driverPhone);
            transport.setVehiclePlate(vehiclePlate);
            transport.setTransportCost(transportCost);

            // Lógica para determinar si está entregado
            boolean delivered = "Entregado".equalsIgnoreCase(status);
            transport.setDelivered(delivered); // sigue usando boolean internamente

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
        Transport transport = transportService.getTransportById(idTransport)
                .orElseThrow(() -> new RuntimeException("Transporte no encontrado"));

        model.addAttribute("transport", transport);
        model.addAttribute("transportTypes", Arrays.asList("Camión de carga", "Furgón de carga", "Pickup de la empresa", "Barco"));
        return "transport/edit";
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
            @RequestParam(required = false, defaultValue = "false") boolean delivered,
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

            transport.setTransportDate(date);
            transport.setTransportType(transport_type);
            transport.setTransportCompany(transport_company);
            transport.setDriverName(driver_name);
            transport.setDriverPhone(driver_phone);
            transport.setDelivered(delivered);
            transport.setVehiclePlate(vehicle_plate);
            transport.setTransportCost(transport_cost);

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
            transportService.deleteTransport(idTransport);
            redirectAttributes.addFlashAttribute("mensaje", "Transporte eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar transporte: " + e.getMessage());
        }

        return "redirect:/transport/list";
    }

    @GetMapping("/view")
    public String viewTransport(@RequestParam("idTransport") int idTransport, Model model) {
        Optional<Transport> transportOpt = transportService.getTransportById(idTransport);

        if (transportOpt.isPresent()) {
            model.addAttribute("transporte", transportOpt.get());
            return "transport/view";
        } else {
            // Manejo de error si no se encuentra
            model.addAttribute("error", "Transporte no encontrado con ID: " + idTransport);
            return "errorPage";
        }
    }

}
