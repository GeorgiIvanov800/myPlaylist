package org.myplaylist.myplaylist.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {


    @GetMapping()
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        return "home";
    }


}
