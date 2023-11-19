package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.myplaylist.myplaylist.service.impl.SongServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("/playlist")
public class PlaylistController {
    private final SongServiceImpl songService;

    private final PlaylistServiceImpl playlistService;

    public PlaylistController(SongServiceImpl songService, PlaylistServiceImpl playlistService) {
        this.songService = songService;
        this.playlistService = playlistService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        List<SongViewModel> songs = songService.getAllSongs();
        model.addAttribute("songs", songs);
        return "playlist-create";
    }


    @PostMapping("/{playlistId}/upload-image")
    public String uploadPlaylistImage(@PathVariable Long playlistId,
                                      @RequestParam("picture") MultipartFile pictureFile) throws IOException {

        String filename = StringUtils.cleanPath(Objects.requireNonNull(pictureFile.getOriginalFilename()));

        playlistService.updatePlaylistImage(playlistId, "/playlist-images/" + filename, pictureFile, filename);

        return "redirect:/users/dashboard";
    }
}
