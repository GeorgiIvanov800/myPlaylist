package org.myplaylist.myplaylist.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
@Entity
@Table(name = "comments")
public class CommentEntity extends BaseEntity {
    @Column(name = "text_content", columnDefinition = "TEXT", nullable = false)
    private String textContent;

    private LocalDateTime createdOn;
    @ManyToOne
    private UserEntity author;
    @ManyToOne
    private PlaylistEntity playlist;

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public PlaylistEntity getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlaylistEntity playlist) {
        this.playlist = playlist;
    }
}
