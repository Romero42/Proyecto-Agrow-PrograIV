package cr.ac.una.agrow.controller.harvest;

import cr.ac.una.agrow.data.harvest.HarvestData;
import cr.ac.una.agrow.domain.harvest.Harvest;
import java.time.LocalDate;
import java.util.LinkedList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HarvestController {

    private HarvestData data;
    private int currentId;

    public HarvestController() {
        this.data = new HarvestData();
    }

    @GetMapping("/harvests/form")
    public String formHarvest(Model model) {
        return "form_harvest";
    }

    @GetMapping("/harvests/edit")
    public String editHarvest(@RequestParam("idHarvest") int id, Model model) {
        setCurrentId(id);
        return "edit_harvest";
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    public int getCurrentId() {
        return this.currentId;
    }

    @PostMapping("/harvests/update")
    public String updateHarvest(
            @RequestParam("typeC") String type,
            @RequestParam("dateC") LocalDate date,
            @RequestParam("totalC") String totalStr,
            @RequestParam("descriptionC") String description,
            @RequestParam("stateC") String quality,
            @RequestParam("destinyC") String destiny,
            RedirectAttributes redirectAttributes) {

        if (!totalStr.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("error", "El campo 'Total' debe contener solo números.");
            return "redirect:/harvests/edit?idHarvest=" + getCurrentId();
        }

        int total = Integer.parseInt(totalStr);

        if (date.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha no puede ser en el futuro.");
            return "redirect:/harvests/edit?idHarvest=" + getCurrentId();
        }

        if (type.length() > 50 || description.length() > 250) {
            redirectAttributes.addFlashAttribute("error", "El tipo de cultivo no debe superar 50 caracteres y la descripción 250 caracteres.");
            return "redirect:/harvests/edit?idHarvest=" + getCurrentId();
        }

        data.updateHarvest(currentId, type, date, total, quality, destiny, true, description);

        return "redirect:/harvests/list";
    }

    @GetMapping("/harvests/list")
    public String listHarvests(Model model,
            @RequestParam(value = "stateC", required = false) String state,
            @RequestParam(value = "destinyC", required = false) String destiny) {

        LinkedList<Harvest> harvestList = data.getHarvested();

        if (state != null && !state.isEmpty()) {
            harvestList = data.getHarvestedByQuality(state);
        }

        if (destiny != null && !destiny.isEmpty()) {
            harvestList = data.getHarvestedByDestiny(destiny);
        }

        model.addAttribute("harvestList", harvestList);
        model.addAttribute("activeModule", "harvests");

        return "list_harvest";
    }

    @PostMapping("/harvests/delete")
    public String deleteHarvest(@RequestParam("idHarvest") int idHarvest, RedirectAttributes redirectAttributes) {
        boolean deleted = data.deleteHarvestById(idHarvest);

        if (deleted) {
            redirectAttributes.addFlashAttribute("mensaje", "Cosecha eliminada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la cosecha.");
        }

        return "redirect:/harvests/list";
    }

    /*
    @GetMapping("/harvests/view")
    public String viewHarvest(@RequestParam("idHarvest") int idHarvest, Model model) {
        Harvest harvest = data.getHarvestById(idHarvest);
        model.addAttribute("harvest", harvest);
        return "view_harvest";
    }*/

    @PostMapping("/harvests/save")
    public String saveHarvest(
            @RequestParam("typeC") String type,
            @RequestParam("dateC") LocalDate date,
            @RequestParam("totalC") String totalStr,
            @RequestParam("descriptionC") String description,
            @RequestParam("stateC") String quality,
            @RequestParam("destinyC") String destiny,
            RedirectAttributes redirectAttributes) {

        if (!totalStr.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("error", "El campo 'Total' debe contener solo números.");
            return "redirect:/harvests/form";
        }

        if (date.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La fecha no puede ser en el futuro.");
            return "redirect:/harvests/form";
        }

        if (type.length() > 50 || description.length() > 250 || destiny.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Tipo máximo 50 caracteres, Descripción 250, Destino 100.");
            return "redirect:/harvests/form";
        }

        int total = Integer.parseInt(totalStr);

        data.saveHarvestInDb(type, date, total, quality, destiny, true, description);

        redirectAttributes.addFlashAttribute("mensaje", "Cosecha registrada correctamente.");
        return "redirect:/harvests/form";
    }
}