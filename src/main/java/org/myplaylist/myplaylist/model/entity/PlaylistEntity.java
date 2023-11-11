package org.myplaylist.myplaylist.model.entity;

import jakarta.persistence.*;
import org.myplaylist.myplaylist.model.enums.PlaylistEnums;

import java.util.List;

@Table(name = "playlists")
@Entity
public class PlaylistEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PlaylistEnums category;

    @Column(nullable = false)
    private String description;
    @ManyToMany
    @JoinTable(name = "playlist_songs", joinColumns = @JoinColumn(name = "playlist_id"),
    inverseJoinColumns = @JoinColumn(name = "song_id"))
    private List<SongEntity> songs;

    public PlaylistEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlaylistEnums getCategory() {
        return category;
    }

    public void setCategory(PlaylistEnums category) {
        this.category = category;
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
}
