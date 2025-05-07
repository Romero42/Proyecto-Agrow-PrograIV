package cr.ac.una.agrow.controller;


import cr.ac.una.Agrow.controller.HomeController;
import cr.ac.una.agrow.domain.User;
import cr.ac.una.agrow.service.User_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("index")
@Controller
public class Index_Controller {

    @Autowired
    private User_Service service;

    @GetMapping("validate")
    public String validateUSer(Model model,
                               @RequestParam String user,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {

        User us = service.getByUser(user);

        if (us != null) {
            if (us.getPassword().equals(password)) {
                HomeController.us = us;
                model.addAttribute("activeModule", "home");

                if (us.getType().equals("Administrador")) {
                    return "index";
                } else {
                    return "/index/user_index";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "La contraseña no coincide");
            }
        } else {

            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        }

        model.addAttribute("fill", true);
        model.addAttribute("user", user);
        model.addAttribute("password", password);
        return "redirect:/";
    }


}
