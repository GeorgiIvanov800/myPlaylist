package org.myplaylist.myplaylist.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {


    @GetMapping()
    public String index() {
        return "index";
    }

    @GetMapping("/users/home")
    public String home(Model model) {
        return "user-home";
    }


}
