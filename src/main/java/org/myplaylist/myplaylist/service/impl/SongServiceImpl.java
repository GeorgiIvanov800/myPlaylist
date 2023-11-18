package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.UserMapper;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl {

    private final SongRepository songRepository;

    private final UserMapper songMapper;

    public SongServiceImpl(SongRepository songRepository, UserMapper songMapper) {
        this.songRepository = songRepository;
        this.songMapper = songMapper;
    }

    public List<SongViewModel> getAllSongs() {
        List<SongEntity> songEntities = songRepository.findAll();
        List<SongViewModel> songs = new ArrayList<>();

        return songEntities.stream()
                .map(songMapper::songEntityToViewModel)
                .collect(Collectors.toList());
    }


}
