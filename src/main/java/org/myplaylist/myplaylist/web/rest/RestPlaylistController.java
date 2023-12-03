package org.myplaylist.myplaylist.web.rest;

import jakarta.validation.Valid;
import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/playlist")
public class RestPlaylistController {
    private final PlaylistServiceImpl playlistService;

    public RestPlaylistController(PlaylistServiceImpl playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody PlaylistBindingModel playlistBindingModel,
                                            BindingResult bindingResult, Principal principal) {
        String username = principal.getName();

        //TODO: Set Limit of 100 songs in a playlist
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.groupingBy(FieldError::getField,
                            Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            playlistService.createPlaylist(playlistBindingModel, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Playlist created successfully"));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    //Fetch songs for specific playlist
    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<List<SongViewModel>> getPlaylistSongs(@PathVariable Long playlistId,
                                                                Principal principal) {
        String email = principal.getName();
        List<SongViewModel> songs = playlistService.getSongsForPlaylist(playlistId, email);
        return ResponseEntity.ok(songs);
    }

    //Populate the fields
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistViewModel> getPlaylist(@PathVariable Long id) {
        PlaylistViewModel playlist = playlistService.findById(id);
        return ResponseEntity.ok(playlist);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updatePlaylist(@PathVariable Long id,
                                            @RequestBody PlaylistBindingModel playlistBindingModel,
                                            BindingResult bindingResult,
                                            Principal principal) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.groupingBy(FieldError::getField,
                            Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        String email = principal.getName();
        // Find the existing playlist by ID
        playlistService.updatePlaylist(id, playlistBindingModel, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Playlist updated successfully"));
    }
}
