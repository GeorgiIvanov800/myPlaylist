package org.myplaylist.myplaylist.model.binding;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.myplaylist.myplaylist.model.enums.PlaylistGenreEnums;

import java.util.List;

public class PlaylistBindingModel {
    @NotEmpty(message = "You should give your playlist a name")
    @Size(min = 3, max = 20 ,message = "Name must be between 3 and 20 characters")
    private String name;
    @NotEmpty(message = "You should give your playlist a simple description")
//    @Size(min = 3, max = 20 ,message = "Description name must be between 3 and 20 characters")
    private String description;
    @NotNull(message = "Please choose a genre for your playlist if you are not sure what the genre is just use Manjda")
    private PlaylistGenreEnums genre;
    @NotNull(message = "Playlist without songs is it really a playlist?!")
    @NotEmpty(message = "Playlist without songs is it really a playlist?!")
    private List<Long> songIds;
    private String pictureUrl;

    public PlaylistBindingModel() {
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

    public List<Long> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<Long> songIds) {
        this.songIds = songIds;
    }

    public PlaylistGenreEnums getGenre() {
        return genre;
    }

    public void setGenre(PlaylistGenreEnums genre) {
        this.genre = genre;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
