package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.binding.CommentBindingModel;
import org.myplaylist.myplaylist.model.view.CommentViewModel;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.service.CommentService;
import org.myplaylist.myplaylist.service.PlaylistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/play")
public class MusicController {

    private final PlaylistService playlistService;
    private final CommentService commentService;

    public MusicController(PlaylistService playlistService, CommentService commentService) {
        this.playlistService = playlistService;
        this.commentService = commentService;
    }

    @GetMapping({"/playlists/{id}"})
    public String playPlaylist(Model model,
                               @PathVariable(name = "id") Long id,
                               Principal principal) {
        System.out.println();
        PlaylistViewModel playlistId = playlistService.findById(id);

        List<CommentViewModel> comments = commentService.findAllByPlaylistId(id);
        model.addAttribute("comments", comments);
        model.addAttribute("playlist", playlistId);


        if (!model.containsAttribute("commentBindingModel")) {
            model.addAttribute("commentBindingModel", new CommentBindingModel());
        }


        return "music-room";
    }
}
