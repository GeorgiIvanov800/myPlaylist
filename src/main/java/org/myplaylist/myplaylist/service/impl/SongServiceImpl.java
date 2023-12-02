package org.myplaylist.myplaylist.service.impl;

import jakarta.transaction.Transactional;
import org.myplaylist.myplaylist.config.PlaylistMapper;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.utils.impl.NextCloudWebDavClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl {

    private final SongRepository songRepository;
    private final PlaylistMapper playlistMapper;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final NextCloudWebDavClient nextCloudWebDavClient;

    public SongServiceImpl(SongRepository songRepository, PlaylistMapper playlistMapper, UserRepository userRepository, PlaylistRepository playlistRepository, NextCloudWebDavClient nextCloudWebDavClient) {
        this.songRepository = songRepository;
        this.playlistMapper = playlistMapper;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.nextCloudWebDavClient = nextCloudWebDavClient;
    }

    public List<SongViewModel> getAllSongs() {
        List<SongEntity> songEntities = songRepository.findAllByUserIsNull();
        List<SongViewModel> songs = new ArrayList<>();

        return songEntities.stream()
                .map(playlistMapper::songEntityToViewModelWithoutOwner)
                .collect(Collectors.toList());
    }


    public List<SongViewModel> getUserSongs(String email) {
        List<SongEntity> userSongs = songRepository.findAllByUser_Email(email);

        return userSongs.stream()
                .map(song -> playlistMapper.songEntityToViewModel(song, email))
                .collect(Collectors.toList());

    }

    @Transactional
    public void deleteSong(Long songId) throws Exception {
        SongEntity songToDelete = songRepository.findById(songId)
                .orElseThrow( () -> new IllegalArgumentException("Cant find song with id" + songId));
        for (PlaylistEntity playlist: songToDelete.getPlaylists()) {
            playlist.getSongs().remove(songToDelete);
            playlistRepository.save(playlist);
        }
        nextCloudWebDavClient.deleteFile(songToDelete.getNextCloudPath());
        songRepository.deleteById(songId);
    }

    public boolean isOwner(Long id, String email) {
        return isOwner(
                songRepository.findById(id).orElse(null),
                email
        );
    }

    private boolean isOwner(SongEntity songEntity, String email) {
        if (songEntity == null || email == null) {
            // anonymous users own no songs
            // missing songs are meaningless
            return false;
        }

        UserEntity viewerEntity =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown user with email..." + email));

        if (isAdmin(viewerEntity)) {
            // all admins own all offers
            return true;
        }

        return Objects.equals(
                songEntity.getUser().getId(),
                viewerEntity.getId());
    }
    private boolean isAdmin(UserEntity userEntity) {
        return userEntity
                .getRoles()
                .stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(r -> UserRoleEnum.ADMIN == r);
    }
}
