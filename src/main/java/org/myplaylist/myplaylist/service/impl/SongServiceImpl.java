package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.PlaylistMapper;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl {

    private final SongRepository songRepository;
    private final PlaylistMapper playlistMapper;

    public SongServiceImpl(SongRepository songRepository, PlaylistMapper playlistMapper) {
        this.songRepository = songRepository;
        this.playlistMapper = playlistMapper;
    }

    public List<SongViewModel> getAllSongs() {
        List<SongEntity> songEntities = songRepository.findAllByUserIsNull();
        List<SongViewModel> songs = new ArrayList<>();

        return songEntities.stream()
                .map(playlistMapper::songEntityToViewModel)
                .collect(Collectors.toList());
    }


    public List<SongViewModel> getUserSongs(String email) {
        List<SongEntity> userSongs = songRepository.findAllByUser_Email(email);

        return userSongs.stream()
                .map(playlistMapper::songEntityToViewModel)
                .collect(Collectors.toList());
    }
}
