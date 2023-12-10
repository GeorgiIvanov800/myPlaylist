package org.myplaylist.myplaylist.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.enums.UserStatus;
import org.myplaylist.myplaylist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;


@Controller
@RequestMapping("/users")
public class UserLoginController {


    @GetMapping("/login")
    public String login(HttpServletRequest request,
                        Model model) {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginError") != null) {
            model.addAttribute("loginError", session.getAttribute("loginError"));
            model.addAttribute("email", session.getAttribute("email"));
            session.removeAttribute("loginError");
        }
        return "user-login";
    }

}