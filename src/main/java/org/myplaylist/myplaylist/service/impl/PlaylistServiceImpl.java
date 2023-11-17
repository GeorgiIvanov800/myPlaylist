package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.UserMapper;
import org.myplaylist.myplaylist.exception.LoginCredentialsException;
import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.enums.PlaylistGenreEnums;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistServiceImpl {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository, UserRepository userRepository, UserMapper mapper) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
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
        UserEntity user = userOptional.get();
        playlist.setUser(user);



        List<SongEntity> songs = songRepository.findAllById(playlistBindingModel.getSongIds());

        playlist.setSongs(songs);

        LOGGER.info("Saving playlist {}", playlist.getName());
        playlistRepository.save(playlist);

    }

    public Page<PlaylistViewModel> getUserPlaylist(Pageable pageable, Long userId) {

        Page<PlaylistEntity> byUser = playlistRepository.findByUserId(userId, pageable);

        return byUser.map(mapper::playlistEntityToViewModel);

    }
}
