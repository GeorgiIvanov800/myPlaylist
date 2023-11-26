package org.myplaylist.myplaylist.config;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @Mapping(target = "formattedDuration", expression = "java(song.getDuration().toMinutes() + \":\" + song.getDuration().getSeconds() % 60)")
    SongViewModel songEntityToViewModel(SongEntity song);
    PlaylistViewModel playlistEntityToViewModel(PlaylistEntity playlistEntity);

}
