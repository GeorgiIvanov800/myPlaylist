package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.view.SongViewModel;

import java.util.List;

public interface SongService {
    List<SongViewModel> getAllSongs();
    List<SongViewModel> getUserSongs(String email);
    void deleteSong(Long songId) throws Exception;
    boolean isOwner(Long id, String email);
}
