package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final PlaylistServiceImpl playlistService;

    public HomeController(PlaylistServiceImpl playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping()
    public String index(Model model,
                        @PageableDefault(
                                size = 6,
                                sort = "createdOn",
                                direction = Sort.Direction.DESC
                        ) Pageable pageable) {

        Page<PlaylistViewModel> latestCreatedPlaylists = playlistService.findByLatestCreated(pageable);
        model.addAttribute("playlist", latestCreatedPlaylists);

        return "index";
    }

//    @GetMapping("/home")
//    public String home(Model model) {
//        return "home";
//    }


}
