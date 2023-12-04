package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.service.UploadFilesService;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.myplaylist.myplaylist.service.impl.SongServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("/playlist")
public class PlaylistController {
    private final SongServiceImpl songService;

    private final UploadFilesService uploadFilesService;

    private final PlaylistServiceImpl playlistService;

    public PlaylistController(SongServiceImpl songService, UploadFilesService uploadFilesService, PlaylistServiceImpl playlistService) {
        this.songService = songService;
        this.uploadFilesService = uploadFilesService;
        this.playlistService = playlistService;
    }

    @GetMapping({"/create", "/create/{id}"})
    public String createOrUpdatePlaylist(Model model,
                                         @PathVariable(name = "id", required = false) Long id,
                                         Principal principal) {
        String email = principal.getName();
        List<SongViewModel> songs = songService.getAllSongs();

        List<SongViewModel> userSongs = songService.getUserSongs(email);

        if (id != null) {
            // Load the playlist by id and add it to the model
            PlaylistViewModel playlist = playlistService.findById(id);
            model.addAttribute("playlist", playlist);

        }

        model.addAttribute("songs", songs);
        model.addAttribute("userSongs", userSongs);

        return "playlist-create";
    }

    @PostMapping("/upload")
    public String uploadSongs(@RequestParam("files") MultipartFile[] files,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {

        if (files.length > 5) {
            redirectAttributes.addFlashAttribute("message", "You can only upload up to 5 songs.");
            return "redirect:/playlist/create";
        }

        if (files.length == 0 || Arrays.stream(files).anyMatch(MultipartFile::isEmpty)) {
            redirectAttributes.addFlashAttribute("error", "No files were selected for upload.");
            return "redirect:/playlist/create"; // Redirect back to the upload page
        }

        String email = principal.getName();
        boolean isSuccessful = uploadFilesService.upload(email, files);

        if (!isSuccessful) {
            redirectAttributes.addFlashAttribute("message", "Hmm something went wrong please try again");
            return "redirect:/playlist/create";
        }

        return "redirect:/playlist/create";
    }
    @PreAuthorize("@songServiceImpl.isOwner(#id, #principal.username)")
    @DeleteMapping("delete/{songId}")
    public String deleteSong(@PathVariable("songId") Long id,
                         @AuthenticationPrincipal UserDetails principal) throws Exception {

        songService.deleteSong(id);

        return "redirect:/playlist/create";

    }
}

