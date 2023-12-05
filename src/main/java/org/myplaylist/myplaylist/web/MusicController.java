package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/play")
public class MusicController {

    private final PlaylistServiceImpl playlistService;

    public MusicController(PlaylistServiceImpl playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping({"/playlists/{id}"})
    public String playPlaylist(Model model,
                               @PathVariable(name = "id") Long id,
                               Principal principal) {
        PlaylistViewModel playlistId = playlistService.findById(id);
        model.addAttribute("playlist", playlistId);

        return "music-room";
    }
}
