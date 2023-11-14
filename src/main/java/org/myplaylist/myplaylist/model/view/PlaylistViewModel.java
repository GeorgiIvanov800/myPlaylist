package org.myplaylist.myplaylist.model.view;

import java.time.Duration;
import java.util.List;

public class PlaylistViewModel {
    private String name;
    private String description;
    private String genre;
    private List<SongViewModel> songs;
    private Duration time;
    private UserViewModel user;

    public PlaylistViewModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<SongViewModel> getSongs() {
        return songs;
    }

    public void setSongs(List<SongViewModel> songs) {
        this.songs = songs;
    }

    public Duration getTime() {
        return time;
    }

    public void setTime(Duration time) {
        this.time = time;
    }

    public UserViewModel getUser() {
        return user;
    }

    public void setUser(UserViewModel user) {
        this.user = user;
    }
}
