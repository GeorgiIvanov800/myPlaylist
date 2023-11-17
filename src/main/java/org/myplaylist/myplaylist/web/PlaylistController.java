package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.service.impl.SongServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/playlist")
public class PlaylistController {

    private final SongServiceImpl songService;

    public PlaylistController(SongServiceImpl songService) {
        this.songService = songService;
    }

    @GetMapping("/create")
    public String create(Model model,
                         @PageableDefault(size = 20)
                         Pageable pageable) {
        Page<SongViewModel> songs = songService.getAllSongs(pageable);
        model.addAttribute("songsPage", songs);
        return "playlist-create";
    }
}
