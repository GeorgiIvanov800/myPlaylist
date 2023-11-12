package org.myplaylist.myplaylist.model.entity;

import jakarta.persistence.*;
import org.myplaylist.myplaylist.model.enums.PlaylistGenreEnums;

import java.util.List;
import java.util.Optional;

@Table(name = "playlists")
@Entity
public class PlaylistEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PlaylistGenreEnums genre;

    private String pictureURL;

    @Column(nullable = false)
    private String description;
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

    public List<SongEntity> getSongs() {
        return songs;
    }

    public void setSongs(List<SongEntity> songs) {
        this.songs = songs;
    }

    public String getPictureURL() {
        return pictureURL;
    }
    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
