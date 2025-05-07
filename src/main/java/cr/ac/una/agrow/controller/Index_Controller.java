package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.User;
import cr.ac.una.agrow.service.User_Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("index")
@Controller
public class Index_Controller {

    @Autowired
    private User_Service service;

    @PostMapping("validate") // Cambiado a @PostMapping
    public String validateUSer(Model model,
                               @RequestParam String user,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {

        User us = service.getByUser(user);

        if (us != null) {
            if (us.getPassword().equals(password)) {
                session.setAttribute("loggedInUser", us); // Guardar usuario en sesión

                return "redirect:/homeIndex";
            } else {
                redirectAttributes.addFlashAttribute("error", "La contraseña no coincide");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        }

        // Si la validación falla, redirigir de nuevo al login
        redirectAttributes.addFlashAttribute("fill", true);
        redirectAttributes.addFlashAttribute("user", user);
        // No pasar password de vuelta por seguridad
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate(); // Invalidar la sesión
        redirectAttributes.addFlashAttribute("mensaje", "Has cerrado sesión exitosamente.");
        return "redirect:/login"; // Redirigir al login
    }
}