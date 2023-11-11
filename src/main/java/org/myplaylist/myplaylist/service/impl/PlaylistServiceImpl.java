package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistServiceImpl {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
    }

    public PlaylistBindingModel createPlaylist(PlaylistBindingModel playlistBindingModel) {

        PlaylistEntity playlist = new PlaylistEntity();
        playlist.setName(playlistBindingModel.getName());
        playlist.setDescription(playlistBindingModel.getDescription());



        List<SongEntity> songs = songRepository.findAllById(playlistBindingModel.getSongIds());
        playlist.setSongs(songs);


        return null;
    }
}
