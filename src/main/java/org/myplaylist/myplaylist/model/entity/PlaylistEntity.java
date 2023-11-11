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
    private List<PlaylistEnums> categories;

    @Column(nullable = false)
    private String description;
    @OneToMany
    private List<SongEntity> songs;

    public PlaylistEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlaylistEnums> getCategories() {
        return categories;
    }

    public void setCategories(List<PlaylistEnums> categories) {
        this.categories = categories;
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
