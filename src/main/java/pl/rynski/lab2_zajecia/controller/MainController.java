package pl.rynski.lab2_zajecia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rynski.lab2_zajecia.model.AppUser;
import pl.rynski.lab2_zajecia.service.UserService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    private UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new AppUser());
        return "registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute AppUser user, HttpServletRequest request) throws MessagingException {
        userService.addNewUser(user, request);
        return "redirect:/login";
    }

    @GetMapping("/verify-token")
    public String verifyToken(@RequestParam String token) {
        userService.verifyToken(token);
        return "redirect:/login";
    }
}
