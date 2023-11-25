package org.myplaylist.myplaylist.model.view;

import java.time.Duration;

public record SongViewModel(
     Long id,
     String title,
     String artist,
     String album,
     String genre,
     String filePath,
     Duration duration
) {}
