package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.init.MusicLibraryFeed;
import org.myplaylist.myplaylist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/users")
public class UserLoginController {

    private final UserService userService;

    private final MusicLibraryFeed musicLibraryFeed;

    public UserLoginController(UserService userService, MusicLibraryFeed musicLibraryFeed) {
        this.userService = userService;
        this.musicLibraryFeed = musicLibraryFeed;
    }


    @GetMapping("/login")
    public String login() {

        return "user-login";
    }

    @PostMapping("/login-error")
    public String onFailure(
            @ModelAttribute("email") String email,
            Model model) {

        model.addAttribute("email", email);
        model.addAttribute("bad_credentials", "true");

        return "user-login";
    }

}
