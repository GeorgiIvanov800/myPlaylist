package org.myplaylist.myplaylist.config;

import org.mapstruct.*;
import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.PlaylistRatingEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.enums.RatingType;
import org.myplaylist.myplaylist.model.view.PlaylistRatingViewModel;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {

    PlaylistEntity playListBindingModelToEntity(PlaylistBindingModel playlistBindingModel);
    @Mapping(target = "formattedDuration", source = "duration")
    @Mapping(target = "userIsOwner", expression = "java(isOwner(song, currentUserEmail))")
    SongViewModel songEntityToViewModel(SongEntity song, @Context String currentUserEmail);

    @Mapping(target = "formattedDuration", source = "duration")
    SongViewModel songEntityToViewModelWithoutOwner(SongEntity song);

    @Mapping(target = "likeCount", ignore = true) // Ignore auto mapping
    @Mapping(target = "dislikeCount", ignore = true)
    PlaylistViewModel playlistEntityToViewModel(PlaylistEntity playlistEntity);

    @AfterMapping
    default void calculateRatings(PlaylistEntity playlistEntity, @MappingTarget PlaylistViewModel playlistViewModel) {
        int likeCount = (int) playlistEntity.getRatings().stream()
                .filter(r -> r.getRatingType() == RatingType.LIKE)
                .count();
        int dislikeCount = (int) playlistEntity.getRatings().stream()
                .filter(r -> r.getRatingType() == RatingType.DISLIKE)
                .count();

        playlistViewModel.setLikeCount(likeCount);
        playlistViewModel.setDislikeCount(dislikeCount);
    }

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
