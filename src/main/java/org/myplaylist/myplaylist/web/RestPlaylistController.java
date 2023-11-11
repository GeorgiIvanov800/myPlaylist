package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playlist")
public class RestPlaylistController {

    private final PlaylistServiceImpl playlistService;

    public RestPlaylistController(PlaylistServiceImpl playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@RequestBody PlaylistBindingModel playlistBindingModel) {

        try {
            PlaylistBindingModel newPlaylist = playlistService.createPlaylist(playlistBindingModel);
            return new ResponseEntity<>(newPlaylist, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
