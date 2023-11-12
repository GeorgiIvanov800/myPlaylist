package org.myplaylist.myplaylist.service.impl;

import org.myplaylist.myplaylist.config.UserMapper;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.repository.SongRepository;
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

    public List<SongViewModel> findAllSongs() {
        List<SongEntity> songs = songRepository.findAll();

        System.out.println("-------------");
        List<SongViewModel> collect = songs.stream().map(songMapper::songEntityToViewModel)
                .collect(Collectors.toList());
        System.out.println("-------------");

        return collect;
    }


}
