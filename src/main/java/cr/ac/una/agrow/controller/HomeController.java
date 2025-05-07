package cr.ac.una.Agrow.controller;

import cr.ac.una.agrow.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    public static User us;

    @GetMapping({"", "home"})
    public String home(Model model) {

        model.addAttribute("fill", false);
        return "/index/validation_index";
    }

    @GetMapping("homeIndex")
    public String homeIndex(Model model) {
        String htlm = "";
        model.addAttribute("activeModule", "home");
        model.addAttribute("name", us.getName());

        if(us.getType().equals("admin")){

            htlm = "index";
        }else {

            htlm = "/index/user_index";
        }

        return htlm;
    }
}
