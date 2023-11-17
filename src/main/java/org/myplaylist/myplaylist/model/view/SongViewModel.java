package org.myplaylist.myplaylist.model.view;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ManyToMany;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.utils.DurationConverter;

import java.time.Duration;
import java.util.List;

public record SongViewModel(
     Long id,
     String title,
     String artist,
     String album,
     String genre
) {}
