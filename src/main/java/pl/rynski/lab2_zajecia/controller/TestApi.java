package pl.rynski.lab2_zajecia.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
public class TestApi {

    @GetMapping("/forAll")
    public String forAll() {
        return "For all";
    }

    @GetMapping("/forUsers")
    public String forUsers(Principal principal, HttpServletRequest request) {
/*        System.out.println(principal.getName());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());*/
        return "Hello user: " +principal.getName();
    }

    @GetMapping("/forAdmin")
    public String forAdmin(Principal principal) {
        return "Hello admin: " + principal.getName();
    }

}
