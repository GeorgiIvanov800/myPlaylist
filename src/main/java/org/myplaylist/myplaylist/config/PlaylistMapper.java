package org.myplaylist.myplaylist.config;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @Mapping(target = "formattedDuration", source = "duration")
    @Mapping(target = "userIsOwner", expression = "java(isOwner(song, currentUserEmail))")
    SongViewModel songEntityToViewModel(SongEntity song, @Context String currentUserEmail);

    @Mapping(target = "formattedDuration", source = "duration")
    SongViewModel songEntityToViewModelWithoutOwner(SongEntity song);
    PlaylistViewModel playlistEntityToViewModel(PlaylistEntity playlistEntity);


    default String formatDuration(Duration duration) {
        if (duration == null) {
            return null;
        }
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    default boolean isOwner(SongEntity song, String currentUserEmail) {
        if (song.getUser() == null || song.getUser().getEmail() == null) {
            return false;
        }
        return song.getUser().getEmail().equals(currentUserEmail);
    }
}
