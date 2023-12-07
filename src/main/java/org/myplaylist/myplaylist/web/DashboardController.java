package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.service.PlaylistService;
import org.myplaylist.myplaylist.service.UploadFilesService;
import org.myplaylist.myplaylist.service.impl.CustomUserDetails;
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
import java.security.Principal;
import java.util.Objects;


@Controller
public class DashboardController {
    private final PlaylistService playlistService;
    private final UploadFilesService uploadFilesService;

    public DashboardController(PlaylistService playlistService, UploadFilesService uploadFilesService) {
        this.playlistService = playlistService;
        this.uploadFilesService = uploadFilesService;
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

    @PostMapping("/users/dashboard/upload-image/{playlistId}")
    public String uploadPlaylistImage(@PathVariable Long playlistId,
                                      @RequestParam("picture") MultipartFile pictureFile,
                                      Principal principal) throws IOException {
        if (pictureFile != null && !pictureFile.isEmpty()) {
            String contentType = pictureFile.getContentType();

            // Check if the file is larger than 1 MB
            if (pictureFile.getSize() > 1048576) {

                return "redirect:/users/dashboard";
            }

            // Check if the file is JPG or PNG
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {

                return "redirect:/users/dashboard";
            }
        }
        uploadFilesService.uploadPlaylistImage(playlistId, pictureFile, principal.getName());

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
