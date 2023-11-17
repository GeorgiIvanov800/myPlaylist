package org.myplaylist.myplaylist.web;

import jakarta.validation.Valid;
import org.myplaylist.myplaylist.exception.CustomValidationException;
import org.myplaylist.myplaylist.model.binding.UserRegistrationBindingModel;
import org.myplaylist.myplaylist.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/users")
public class UserRegisterController {

    private final UserService userService;


    public UserRegisterController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    public String register(Model model) {

        return "user-register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid UserRegistrationBindingModel userRegistrationBindingModel,
                               BindingResult bindingResult, RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors() || !userRegistrationBindingModel.getPassword()
                .equals(userRegistrationBindingModel.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("userRegistrationBindingModel", userRegistrationBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegistrationBindingModel", bindingResult);

            return "redirect:register";
        }

        try {
            userService.registerUser(userRegistrationBindingModel);
            redirectAttributes.addFlashAttribute("message", "Registration successful!");
            return "redirect:user-login";

        } catch (CustomValidationException e) {
            redirectAttributes.addFlashAttribute("userRegistrationBindingModel", userRegistrationBindingModel);
            if ("username".equals(e.getFiled())) {
                redirectAttributes.addFlashAttribute("usernameError", e.getMessage());
            } else if ("email".equals(e.getFiled())) {
                redirectAttributes.addFlashAttribute("emailError", e.getMessage());
            }
            return "redirect:register";
        }
    }

    @ModelAttribute
    UserRegistrationBindingModel userRegistrationBindingModel() {
        return new UserRegistrationBindingModel();
    }
}