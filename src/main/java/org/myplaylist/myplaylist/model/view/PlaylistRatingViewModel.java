package org.myplaylist.myplaylist.model.view;

import org.myplaylist.myplaylist.model.enums.RatingType;

public record PlaylistRatingViewModel(
        Long id,
        Long userId,
        RatingType rating
){}