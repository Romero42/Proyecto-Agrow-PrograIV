package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping({"", "/", "/login"})
    public String loginPage(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "redirect:/homeIndex";
        }
        model.addAttribute("fill", false);
        return "login";
    }

    @GetMapping("homeIndex")
    public String homeIndex(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("activeModule", "home");
        model.addAttribute("userName", loggedInUser.getName());
        model.addAttribute("userType", loggedInUser.getType());

        // Siempre redirigimos a la vista 'index'
        return "index";
    }
}