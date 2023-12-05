package org.myplaylist.myplaylist.service;

import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.model.enums.RatingType;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PlaylistService {

    void createPlaylist(PlaylistBindingModel playlistBindingModel, String email);

    Page<PlaylistViewModel> getUserPlaylist(Pageable pageable, Long userId);

    Long getTotalSongCountForUser(Long userId);

    void updatePlaylistImage(Long playlistId, String pictureUrl, MultipartFile pictureFile, String filename) throws IOException;

    List<SongViewModel> getSongsForPlaylist(Long playlistId, String email);

    void updatePlaylist(Long playlistId, PlaylistBindingModel playlistBindingModel, String email);

    PlaylistViewModel findById(Long id);

    void deletePlaylist(Long id);

    boolean isOwner(Long id, String email);

    Map<String, Integer> ratePlaylist(Long id, String username, RatingType ratingType);

    Page<PlaylistViewModel> findByLatestCreated(Pageable pageable);

    Page<PlaylistViewModel> topRatedPlaylists(Pageable pageable);
}
