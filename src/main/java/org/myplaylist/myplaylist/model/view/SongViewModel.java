package org.myplaylist.myplaylist.model.view;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ManyToMany;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.utils.DurationConverter;

import java.time.Duration;
import java.util.List;

public class SongViewModel {
    private Long id;
    private String title;
    private String artist;
    private String album;
    private String genre;

    public SongViewModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
