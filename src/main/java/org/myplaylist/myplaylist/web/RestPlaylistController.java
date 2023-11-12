package org.myplaylist.myplaylist.web;

import jakarta.validation.Valid;
import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.myplaylist.myplaylist.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/playlist")
public class RestPlaylistController {

    private final PlaylistServiceImpl playlistService;


    private static final Logger LOGGER = LoggerFactory.getLogger(RestPlaylistController.class);

    public RestPlaylistController(PlaylistServiceImpl playlistService, UserServiceImpl userService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody PlaylistBindingModel playlistBindingModel,
                                            BindingResult bindingResult, Principal principal) {
        String username = principal.getName();


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
}
