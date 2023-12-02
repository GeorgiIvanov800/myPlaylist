package org.myplaylist.myplaylist.config;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @Mapping(target = "formattedDuration", expression = "java(song.getDuration().toMinutes() + \":\" + song.getDuration().getSeconds() % 60)")
    @Mapping(target = "userIsOwner", expression = "java(isOwner(song, currentUserEmail))")
    SongViewModel songEntityToViewModel(SongEntity song, @Context String currentUserEmail);

    SongViewModel songEntityToViewModelWithoutOwner(SongEntity song);
    PlaylistViewModel playlistEntityToViewModel(PlaylistEntity playlistEntity);


    default boolean isOwner(SongEntity song, String currentUserEmail) {
        return song.getUser().getEmail().equals(currentUserEmail);
    }
}
