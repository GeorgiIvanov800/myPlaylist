package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.UserMapper;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<SongViewModel> getAllSongs(Pageable pageable) {
        Page<SongEntity> songEntities = songRepository.findAll(pageable);

        return songEntities.map(songMapper::songEntityToViewModel);
    }


}
