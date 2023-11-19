package org.myplaylist.myplaylist.model.view;

import java.util.Set;

public record PlaylistViewModel(
        Long id,
        String name,
        String genre,
        String pictureUrl,
        String description,
        Set<SongViewModel> songs,
        UserViewModel user
) {}
