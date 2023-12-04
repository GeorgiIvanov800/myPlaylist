package org.myplaylist.myplaylist.model.view;

import java.time.LocalDateTime;
import java.util.Set;

public class PlaylistViewModel {
    private Long id;
    private String name;
    private String genre;
    private String pictureUrl;
    private String description;
    private LocalDateTime createdOn;
    private Set<SongViewModel> songs;
    private UserViewModel user;
    private int likeCount;
    private int dislikeCount;

    public PlaylistViewModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public Set<SongViewModel> getSongs() {
        return songs;
    }

    public void setSongs(Set<SongViewModel> songs) {
        this.songs = songs;
    }

    public UserViewModel getUser() {
        return user;
    }

    public void setUser(UserViewModel user) {
        this.user = user;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
}
