package org.myplaylist.myplaylist.model.view;

import java.util.List;

public record PlaylistViewModel(
        Long id,
        String name,
        String genre,
        String pictureUrl,
        String description,
        List<SongViewModel> songs,
        UserViewModel user
) {}
