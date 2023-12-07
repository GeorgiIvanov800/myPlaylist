package org.myplaylist.myplaylist.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadFilesService {

    boolean uploadSongs(String username, MultipartFile[] files);

    boolean uploadPlaylistImage(Long playlistId, MultipartFile imageFile, String email);

}
