package org.myplaylist.myplaylist.web;

import jakarta.validation.Valid;
import org.myplaylist.myplaylist.model.binding.CommentBindingModel;
import org.myplaylist.myplaylist.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


@Controller
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public String createComment(@Valid CommentBindingModel commentBindingModel,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {
        String userEmail = principal.getName();

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentBindingModel", commentBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.commentBindingModel", bindingResult);
            return "redirect:/play/playlists/" + commentBindingModel.getPlaylistId();
        }

        commentService.create(commentBindingModel, userEmail);

        return "redirect:/play/playlists/" + commentBindingModel.getPlaylistId();
    }

    @ModelAttribute
    CommentBindingModel commentBindingModel() {
        return new CommentBindingModel();
    }
}
