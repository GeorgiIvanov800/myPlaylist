package org.myplaylist.myplaylist.model.entity;

import jakarta.persistence.*;
import org.myplaylist.myplaylist.model.enums.RatingType;

@Entity
@Table(name = "playlist_ratings")
public class PlaylistRatingEntity extends BaseEntity {
    @ManyToOne
    private PlaylistEntity playlist;
    @ManyToOne
    private UserEntity user;
    @Enumerated(EnumType.STRING)
    private RatingType ratingType;

    public PlaylistEntity getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlaylistEntity playlist) {
        this.playlist = playlist;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public RatingType getRatingType() {
        return ratingType;
    }

    public void setRatingType(RatingType ratingType) {
        this.ratingType = ratingType;
    }
}
