package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.service.impl.CustomUserDetails;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;


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
                     size = 6,
                     sort = "id"
             ) Pageable pageable) {
        Long id = user.getUserId();

        Page<PlaylistViewModel> userPlaylist = playlistService.getUserPlaylist(pageable, id);
        Long songsCount = playlistService.getTotalSongCountForUser(id);
        model.addAttribute("playlist", userPlaylist);
        model.addAttribute("songsCount", songsCount);

        return "user-dashboard";
    }

    @PostMapping("dashboard/upload-image/{playlistId}")
    public String uploadPlaylistImage(@PathVariable Long playlistId,
                                      @RequestParam("picture") MultipartFile pictureFile) throws IOException {

        String filename = StringUtils.cleanPath(Objects.requireNonNull(pictureFile.getOriginalFilename()));

        playlistService.updatePlaylistImage(playlistId, "/playlist-images/" + filename, pictureFile, filename);

        return "redirect:/users/dashboard";
    }


    @PreAuthorize("@playlistServiceImpl.isOwner(#id, #principal.username)")
    @DeleteMapping("/users/dashboard/deletePlaylist/{playlistId}")
    public String deletePlaylist(@PathVariable("playlistId") Long id,
                                 @AuthenticationPrincipal UserDetails principal) {

        playlistService.deletePlaylist(id);

        return "redirect:/users/dashboard";

    }

}
