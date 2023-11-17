package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.service.impl.CustomUserDetails;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DashboardController {

    private final PlaylistServiceImpl playlistService;

    public DashboardController(PlaylistServiceImpl playlistService) {
        this.playlistService = playlistService;
    }


    @GetMapping()
    public String index() {
        return "index";
    }

    @GetMapping("/users/dashboard")
    public String dashboard
            (@AuthenticationPrincipal CustomUserDetails user,
             Model model,
             @PageableDefault(
                     size = 3,
                     sort = "id"
             ) Pageable pageable) {
        Long id = user.getUserId();

        Page<PlaylistViewModel> userPlaylist = playlistService.getUserPlaylist(pageable, id);

        model.addAttribute("playlist", userPlaylist);

        return "user-dashboard";
    }


}
