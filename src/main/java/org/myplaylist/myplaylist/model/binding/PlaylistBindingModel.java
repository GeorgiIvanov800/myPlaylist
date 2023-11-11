package org.myplaylist.myplaylist.model.binding;

import org.myplaylist.myplaylist.model.enums.PlaylistEnums;

import java.util.List;

public class PlaylistBindingModel {

    private String name;

    private String description;

    private List<Long> songIds;

    private List<PlaylistEnums> categories;

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

    public List<PlaylistEnums> getCategories() {
        return categories;
    }

    public void setCategories(List<PlaylistEnums> categories) {
        this.categories = categories;
    }
}
