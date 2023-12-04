package org.myplaylist.myplaylist.model.view;

import org.myplaylist.myplaylist.model.entity.PlaylistRatingEntity;

import java.time.LocalDateTime;
import java.util.Set;

public record PlaylistViewModel(
        Long id,
        String name,
        String genre,
        String pictureUrl,
        String description,
        LocalDateTime createdOn,
        Set<SongViewModel> songs,
        UserViewModel user,
        Set<PlaylistRatingViewModel> ratings
) {}
