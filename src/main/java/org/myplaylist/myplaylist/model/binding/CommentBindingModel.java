package org.myplaylist.myplaylist.model.binding;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.myplaylist.myplaylist.model.entity.UserEntity;

import java.time.LocalDateTime;

public class CommentBindingModel {
    @NotEmpty(message = "Can't post an empty comment")
    private String textContent;

    private LocalDateTime createdOn;

    private Long userId;

    private Long playlistId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }
}
