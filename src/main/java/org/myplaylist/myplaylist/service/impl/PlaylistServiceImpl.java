package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.PlaylistMapper;
import org.myplaylist.myplaylist.exception.LoginCredentialsException;
import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.enums.PlaylistGenreEnums;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlaylistMapper playlistMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistServiceImpl.class);
    private static final String UPLOAD_DIR = "/home/givanov/IdeaProjects/myplaylist/src/main/resources/static/playlist-images/";

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository, UserRepository userRepository, PlaylistMapper playlistMapper) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.playlistMapper = playlistMapper;
    }

    public void createPlaylist(PlaylistBindingModel playlistBindingModel, String username) {

        PlaylistEntity playlist = new PlaylistEntity();
        playlist.setName(playlistBindingModel.getName());
        playlist.setDescription(playlistBindingModel.getDescription());
        playlist.setGenre(PlaylistGenreEnums.valueOf(String.valueOf(playlistBindingModel.getGenre())));

        Optional<UserEntity> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            throw new LoginCredentialsException("User not found: " + username);
        }

        playlist.setUser(userOptional.get());

        //get the songs by id
//        List<SongEntity> songList = songRepository.findAllById(playlistBindingModel.getSongIds());

        List<SongEntity> songList = new ArrayList<>();
        for (Long songId : playlistBindingModel.getSongIds()) {
            songRepository.findById(songId).ifPresent(song -> {
                if (!songList.contains(song)) {
                    songList.add(song);
                }
            });
        }
        playlist.setSongs(songList);

        LOGGER.info("Saving playlist {}", playlist.getName());
        playlistRepository.save(playlist);
    }

    public Page<PlaylistViewModel> getUserPlaylist(Pageable pageable, Long userId) {

        Page<PlaylistEntity> byUser = playlistRepository.findByUserId(userId, pageable);
        System.out.println();
        return byUser.map(playlistMapper::playlistEntityToViewModel);
    }

    public Long getTotalSongCountForUser(Long userId) {
        return playlistRepository.countTotalSongsByUserId(userId);
    }

    public void updatePlaylistImage(Long playlistId, String pictureUrl, MultipartFile pictureFile, String filename) throws IOException {

        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = pictureFile.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Log error and/or rethrow as custom exception
            throw new IllegalArgumentException("Error saving file: " + filename, e);
        }


        PlaylistEntity playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid playlist ID: " + playlistId));
        playlist.setPictureUrl(pictureUrl);
        playlistRepository.save(playlist);

        System.out.println(playlist.getPictureUrl());
    }

    public List<SongViewModel> getSongsForPlaylist(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> playlist.getSongs().stream()
                        .limit(100)
                        .map(playlistMapper::songEntityToViewModel)
                        .collect(Collectors.toList()))
                .orElseThrow( () -> new IllegalArgumentException("Invalid playlist ID: " + playlistId));
    }
}
