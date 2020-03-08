package pl.rynski.lab2_zajecia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.rynski.lab2_zajecia.model.AppUser;
import pl.rynski.lab2_zajecia.service.UserService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
        model.addAttribute("appUser", new AppUser());
        return "registration";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute AppUser appUser,
                           BindingResult bindingResult,
                           HttpServletRequest request,
                           @RequestParam(defaultValue = "false") boolean adminCheckbox) throws MessagingException {
        if(bindingResult.hasErrors()) {
            return "registration";
        }
        userService.addNewUser(appUser, request);
        if(adminCheckbox) {
            userService.addNewAdmin(appUser, request);
        }
        return "redirect:/login";
    }

    @GetMapping("/verify-token")
    public String verifyToken(@RequestParam String token) {
        userService.verifyToken(token);
        return "redirect:/login";
    }

    @GetMapping("/verify-admin")
    public String verifyAdmin(@RequestParam String token) {
        userService.verifyAdmin(token);
        return "redirect:/login";
    }
}
