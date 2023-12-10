package org.myplaylist.myplaylist.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            // User is logged in, redirect to the dashboard
            return "redirect:/users/dashboard";
        }
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
