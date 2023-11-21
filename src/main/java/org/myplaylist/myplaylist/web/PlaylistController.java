package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.service.UploadFilesService;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.myplaylist.myplaylist.service.impl.SongServiceImpl;
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

    @GetMapping("/create")
    public String create(Model model, Principal principal) {
        String email = principal.getName();

        List<SongViewModel> songs = songService.getAllSongs();

        List<SongViewModel> userSongs = songService.getUserSongs(email);

        model.addAttribute("songs", songs);
        model.addAttribute("userSongs", userSongs);
        return "playlist-create";
    }


    @PostMapping("/{playlistId}/upload-image")
    public String uploadPlaylistImage(@PathVariable Long playlistId,
                                      @RequestParam("picture") MultipartFile pictureFile) throws IOException {

        String filename = StringUtils.cleanPath(Objects.requireNonNull(pictureFile.getOriginalFilename()));

        playlistService.updatePlaylistImage(playlistId, "/playlist-images/" + filename, pictureFile, filename);

        return "redirect:/users/dashboard";
    }

    @PostMapping("/upload")
    public String uploadSongs(@RequestParam("files") MultipartFile[] files,
                              RedirectAttributes redirectAttributes, Principal principal) {
//        TODO:Provide clear feedback for successful uploads and notifications for any issues or errors.
        if (files.length > 20) {
            redirectAttributes.addFlashAttribute("message", "You can only upload up to 20 songs.");
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
}

