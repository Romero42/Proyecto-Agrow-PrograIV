package cr.ac.una.agrow.controller;

import cr.ac.una.agrow.domain.User;
import cr.ac.una.agrow.service.User_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserControlller {

    @Autowired
    private User_Service service;
    private User previewUser;

    //carga la lista de usuarios
    @GetMapping("list")
    public String list(Model model) {

        Pageable pageable = PageRequest.of(0, 5);

        Page<User> userPage = service.getAllUser(pageable);

        String message = null;

        if(userPage.isEmpty()){
            message = "No se puede listar alquileres";
        }

        model.addAttribute("previewType", "none");
        model.addAttribute("validate", message);
        model.addAttribute("currentPage", 0);
        model.addAttribute("activeModule", "user");
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("users",userPage.getContent());
        return "/user/list_user";
    }

    //envia a la vista de guardar
    @GetMapping("saveView")
    public String view(Model model) {

        previewUser = new User();
        model.addAttribute("user", new User());
        model.addAttribute("activeModule", "user");
        return "/user/add_user";
    }

    //carga los datos del usuario que se trato de guardar y fallo
    @GetMapping("saveMemory")
    public String saveMemory(Model model) {

        model.addAttribute("user", previewUser);
        model.addAttribute("activeModule", "user");
        return "/user/add_user";
    }

    //trata de guardar un usuario
    @GetMapping("save")
    public String save(RedirectAttributes redirectAttributes, Model model, @RequestParam String user, @RequestParam String password,
                       @RequestParam String name, @RequestParam String email, @RequestParam String contactNumber,
                       @RequestParam String type) {

        model.addAttribute("activeModule", "user");
        User us = new User(user, password, name, email, contactNumber, type);
        if (service.getByUser(user) != null) {

            previewUser = us;
            redirectAttributes.addFlashAttribute("error", "Usuario no valido, utilice otro");
            return "redirect:/user/saveMemory";
        } else {

            if (service.save(us)) {

                redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado");

            } else {

                previewUser = us;
                redirectAttributes.addFlashAttribute("error", "Usuario no registrado");
                return "redirect:/user/saveMemory";
            }
        }

        return "redirect:/user/list";
    }

    //manda a la vista de actualizar
    @GetMapping("updateView")
    public String viewUpdate(Model model, @RequestParam int id) {

        model.addAttribute("activeModule", "user");
        model.addAttribute("user", service.getById(id));
        return "/user/update_user";
    }

    //trata de actualizar un usuario
    @GetMapping("update")
    public String update(RedirectAttributes redirectAttributes, Model model, @RequestParam int id, @RequestParam String user, @RequestParam String password,
                         @RequestParam String name, @RequestParam String email, @RequestParam String contactNumber,
                         @RequestParam String type) {

        model.addAttribute("activeModule", "user");
        User us = new User(id ,user, password, name, email, contactNumber, type);
        if (service.save(us)) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado");
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no actualizado");
        }

        return "redirect:/user/list";
    }

    //envia y carga la vista de usuario
    @GetMapping("view")
    public String view(Model model, @RequestParam int id) {

        model.addAttribute("activeModule", "user");
        model.addAttribute("user", service.getById(id));
        return "/user/view_user";
    }

    //borra un usuario por id
    @GetMapping("delete")
    public String delete(RedirectAttributes redirectAttributes, Model model, @RequestParam int id) {

        model.addAttribute("activeModule", "user");
        if(service.deleteById(id)){
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado");
        }else {

            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar");
        }
        return "redirect:/user/list";
    }

    //actualiza la tabla con los filtros o pagina
    @GetMapping("tableFilter")
    public String filter(Model model,@RequestParam int page, @RequestParam(value = "user", required = false) String user,
                         @RequestParam(value = "type", required = false) String type){

        Pageable pageable = PageRequest.of(page, 5);

        Page<User> userPage = null;
        List<User> users = new ArrayList<>();

        if (type != null && !type.isEmpty()) {

            System.out.println("entra tipo");
            userPage = service.getByType(type, pageable);

            model.addAttribute("type", true);
            model.addAttribute("totalPages", userPage.getTotalPages());
            model.addAttribute("previewType", type);
            model.addAttribute("users", userPage.getContent());
        }else if (user != null && !user.isEmpty()) {

            System.out.println("entra user");
            User foundUser = service.getByUser(user);
            if (foundUser != null) {
                users.add(foundUser);
            }

            model.addAttribute("type", false);
            model.addAttribute("users", users);
        }else {

            System.out.println("entra normal");
            userPage = service.getAllUser(pageable);

            model.addAttribute("type", true);
            model.addAttribute("totalPages", userPage.getTotalPages());
            model.addAttribute("users", userPage.getContent());
        }

        model.addAttribute("currentPage", page);
        return "/user/table_user";
    }

}
