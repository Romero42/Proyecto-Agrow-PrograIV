package cr.ac.una.agrow.controller.producer;

import cr.ac.una.agrow.domain.producer.Producer;
import cr.ac.una.agrow.data.producer.DataProducer;
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
@RequestMapping("/producers")
public class ProducerController {

    private static final DateTimeFormatter DATE_FORMAT_INPUT = DateTimeFormatter.ISO_LOCAL_DATE;

    @GetMapping("/list")
    public String listProducers(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "id_producer", required = false) Integer id,
            Model model) {

        List<Producer> producers;
        String validationMessage = null;

        if (id != null) {
            Producer producer = DataProducer.getProducer(id);
            producers = new java.util.LinkedList<>();
            if (producer != null) {
                producers.add(producer);
            } else {
                validationMessage = "El productor con Código/ID " + id + " no existe.";
            }
            model.addAttribute("searchId", id);
        } else if (city != null && !city.trim().isEmpty()) {
            producers = DataProducer.getProducersByCity(city.trim());
            if (producers.isEmpty()) {
                validationMessage = "No hay productores registrados en la ciudad seleccionada: " + city;
            }
            model.addAttribute("selectedCity", city);
        } else {
            producers = DataProducer.getAllProducers();
            if (producers.isEmpty()) {
                validationMessage = "No hay productores registrados.";
            }
        }

        model.addAttribute("listP", producers);
        model.addAttribute("validate", validationMessage);
        model.addAttribute("activeModule", "producer");
        model.addAttribute("activePage", "list");
        return "producer/producers_list";
    }

    @GetMapping("/form")
    public String showSaveForm(Model model) {
        Producer producer = new Producer();
        producer.setActive(true);
        model.addAttribute("producer", producer);
        model.addAttribute("isEdit", false);
        model.addAttribute("activeModule", "producer");
        model.addAttribute("activePage", "add");
        return "producer/save_producer";
    }

    @PostMapping("/save")
    public String saveProducer(
            @RequestParam("producerName") String name,
            @RequestParam("contactNumber") String contact,
            @RequestParam("registrationDate") String registrationDateStr,
            @RequestParam("producerType") String type,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            RedirectAttributes redirectAttributes) {

        String error = validateProducerData(name, contact, registrationDateStr, type, email, city, address);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            Producer producerData = new Producer(0, name, contact, null, type, email, city, address, true);
            redirectAttributes.addFlashAttribute("producer", producerData);
            redirectAttributes.addFlashAttribute("registrationDateStr", registrationDateStr);
            return "redirect:/producers/form";
        }

        LocalDate registrationDate;
        try {
            registrationDate = LocalDate.parse(registrationDateStr, DATE_FORMAT_INPUT);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Formato de fecha de registro inválido. Use el selector.");
            Producer producerData = new Producer(0, name, contact, null, type, email, city, address, true);
            redirectAttributes.addFlashAttribute("producer", producerData);
            redirectAttributes.addFlashAttribute("registrationDateStr", registrationDateStr);
            return "redirect:/producers/form";
        }

        Producer p = new Producer(0, name.trim(), contact.trim(), registrationDate, type, email.trim(), city, address.trim(), true);

        String resultMessage = DataProducer.saveProducer(p);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
        } else {
            redirectAttributes.addFlashAttribute("error", message);
            redirectAttributes.addFlashAttribute("producer", p);
            redirectAttributes.addFlashAttribute("registrationDateStr", registrationDateStr);
            return "redirect:/producers/form";
        }

        return "redirect:/producers/list";
    }

    @GetMapping("/edit")
    public String showUpdateForm(
            @RequestParam("id_producer") int id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Producer producer = DataProducer.getProducer(id);

        if (producer == null) {
            redirectAttributes.addFlashAttribute("error", "Productor con ID " + id + " no encontrado.");
            return "redirect:/producers/list";
        }

        model.addAttribute("producer", producer);
        model.addAttribute("isEdit", true);
        model.addAttribute("activeModule", "producer");
        return "producer/update_producer";
    }

    @PostMapping("/update")
    public String updateProducer(
            @RequestParam("id_producer") int id,
            @RequestParam("producerName") String name,
            @RequestParam("contactNumber") String contact,
            @RequestParam("registrationDate") String registrationDateStr,
            @RequestParam("producerType") String type,
            @RequestParam("email") String email,
            @RequestParam("city") String city,
            @RequestParam("address") String address,
            @RequestParam(value = "isActive", defaultValue = "false") boolean active,
            RedirectAttributes redirectAttributes) {

        String error = validateProducerData(name, contact, registrationDateStr, type, email, city, address);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/producers/edit?id_producer=" + id;
        }

        LocalDate registrationDate;
        try {
            registrationDate = LocalDate.parse(registrationDateStr, DATE_FORMAT_INPUT);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("error", "Formato de fecha de registro inválido. Use el selector.");
            return "redirect:/producers/edit?id_producer=" + id;
        }

        Producer p = new Producer(id, name.trim(), contact.trim(), registrationDate, type, email.trim(), city, address.trim(), active);

        String resultMessage = DataProducer.updateProducer(p);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
        } else {
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/producers/edit?id_producer=" + id;
        }

        return "redirect:/producers/list";
    }

    @PostMapping("/delete")
    public String deleteProducerButton(
            @RequestParam("id_producer") int id,
            RedirectAttributes redirectAttributes) {

        String resultMessage = DataProducer.deleteProducer(id);
        String[] parts = Util.arraySplit(resultMessage);
        int typeMsg = Integer.parseInt(parts[0]);
        String message = parts[1];

        if (typeMsg == 1) {
            redirectAttributes.addFlashAttribute("mensaje", message);
        } else {
            redirectAttributes.addFlashAttribute("error", message);
        }

        return "redirect:/producers/list";
    }

    @GetMapping("/information")
    public String information(
            @RequestParam("id_producer") int id,
            Model model, RedirectAttributes redirectAttributes) {

        Producer producer = DataProducer.getProducer(id);
        if (producer == null) {
            redirectAttributes.addFlashAttribute("error", "Productor con ID " + id + " no encontrado.");
            return "redirect:/producers/list";
        }
        model.addAttribute("producer", producer);
        model.addAttribute("activeModule", "producer");
        return "producer/information_producer";
    }

    private String validateProducerData(String name, String contact, String registrationDateStr, String type, String email, String city, String address) {
        if (name == null || name.trim().isEmpty()) return "El nombre es obligatorio.";
        if (!name.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ. ]+$")) return "El nombre solo debe contener letras, puntos y espacios.";
        if (contact == null || contact.trim().isEmpty()) return "El número de teléfono es obligatorio.";
        if (!contact.matches("[246789]\\d{7}")) return "El número de teléfono debe ser válido en Costa Rica (8 dígitos, no iniciar con 1, 3, 5).";
        if (registrationDateStr == null || registrationDateStr.trim().isEmpty()) return "La fecha de registro es obligatoria.";
        try {
            LocalDate regDate = LocalDate.parse(registrationDateStr, DATE_FORMAT_INPUT);
            if (regDate.isAfter(LocalDate.now())) return "La fecha de registro no puede ser futura.";
        } catch (DateTimeParseException e) {
            return "Formato de fecha de registro inválido. Use el selector.";
        }
        if (type == null || type.isEmpty()) return "El tipo de productor es obligatorio.";
        if (email == null || email.trim().isEmpty()) return "El correo electrónico es obligatorio.";
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) return "El formato del correo electrónico no es válido.";
        if (city == null || city.isEmpty()) return "La provincia es obligatoria.";
        if (address == null || address.trim().isEmpty()) return "La dirección es obligatoria.";
        return null;
    }
}
