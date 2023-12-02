package org.myplaylist.myplaylist.model.entity;

import jakarta.persistence.*;
import org.myplaylist.myplaylist.utils.impl.DurationConverter;

import java.time.Duration;
import java.util.Set;

@Entity
@Table(name = "songs")
public class SongEntity extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String artist;
    private String album;
    @Column(columnDefinition = "TEXT")
    private String comment;
    private String genre;
    private Integer year;
    @Convert(converter = DurationConverter.class)
    private Duration duration;
    private String type;
    @Column(columnDefinition = "TEXT")
    private String filePath; //store the location of the song file
    @ManyToMany(mappedBy = "songs")
    private Set<PlaylistEntity> playlists;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public SongEntity() {
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<PlaylistEntity> getPlaylists() {
        return playlists;
    }
    public void setPlaylists(Set<PlaylistEntity> playlists) {
        this.playlists = playlists;
    }
}
