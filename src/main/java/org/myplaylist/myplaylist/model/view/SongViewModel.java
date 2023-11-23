package org.myplaylist.myplaylist.model.view;

public record SongViewModel(
     Long id,
     String title,
     String artist,
     String album,
     String genre,
     String filePath
) {}
