package org.myplaylist.myplaylist.model.entity;

import jakarta.persistence.*;
import org.myplaylist.myplaylist.model.enums.PlaylistGenreEnums;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "playlists")
@Entity
public class PlaylistEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PlaylistGenreEnums genre;
    private String pictureUrl;
    @Column(nullable = false)
    private String description;
    private LocalDateTime createdOn;
    @ManyToMany
    @JoinTable(name = "playlist_songs", joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id"))
    private List<SongEntity> songs;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public PlaylistEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlaylistGenreEnums getGenre() {
        return genre;
    }

    public void setGenre(PlaylistGenreEnums genre) {
        this.genre = genre;
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

    public List<SongEntity> getSongs() {
        return songs;
    }

    public void setSongs(List<SongEntity> songs) {
        this.songs = songs;
    }
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
