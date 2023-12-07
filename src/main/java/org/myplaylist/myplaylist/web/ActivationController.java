package org.myplaylist.myplaylist.web;


import org.myplaylist.myplaylist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ActivationController {
    private final UserService userService;

    public ActivationController(UserService userService) {
        this.userService = userService;

    }
    @GetMapping("/user/activate")
    public String activateUser(@RequestParam("activation_link") String token,
                               RedirectAttributes redirectAttributes) {

        boolean isActive = userService.activateUser(token);

        if (isActive) {
            redirectAttributes.addFlashAttribute("activationSuccess","Your account has been activated successfully. Please log in.");
            return "redirect:/users/login";
        } else {
            redirectAttributes.addFlashAttribute("activationError", "Activation failed. The link may be invalid or expired.");
            return "redirect:/activation-failed";
        }

    }
    @GetMapping("/activation-failed")
    public String showActivationFailedPage() {
        return "activation-failed";
    }
}
