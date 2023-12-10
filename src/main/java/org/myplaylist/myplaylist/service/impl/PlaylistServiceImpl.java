package org.myplaylist.myplaylist.service.impl;

import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.myplaylist.myplaylist.config.mapper.PlaylistMapper;
import org.myplaylist.myplaylist.exception.ObjectNotFoundException;
import org.myplaylist.myplaylist.model.binding.PlaylistBindingModel;
import org.myplaylist.myplaylist.model.entity.*;
import org.myplaylist.myplaylist.model.enums.PlaylistGenreEnums;
import org.myplaylist.myplaylist.model.enums.RatingType;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.service.PlaylistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlaylistMapper playlistMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository, UserRepository userRepository, PlaylistMapper playlistMapper) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.playlistMapper = playlistMapper;
    }

    @Override
    public void createPlaylist(PlaylistBindingModel playlistBindingModel, String email) {

        PlaylistEntity playlist = playlistMapper.playListBindingModelToEntity(playlistBindingModel);

        playlist.setCreatedOn(LocalDateTime.now());
        playlist.setIsPrivate(playlistBindingModel.getIsPrivate());


        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ObjectNotFoundException("User with email: " + email + " not found.");
        }

        playlist.setUser(userOptional.get());

        updateSongsInPlaylist(playlistBindingModel, playlist);

        LOGGER.info("Saving playlist {}", playlist.getName());
        playlistRepository.save(playlist);
    }

    @Override
    public Page<PlaylistViewModel> getUserPlaylist(Pageable pageable, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Page<PlaylistEntity> byUser = playlistRepository.findByUserId(userId, pageable);
        return byUser.map(playlistMapper::playlistEntityToViewModel);
    }

    @Override
    public Long getTotalSongCountForUser(Long userId) {
        return playlistRepository.countTotalSongsByUserId(userId);
    }

    @Override //Get the songs for the playlist
    public List<SongViewModel> getSongsForPlaylist(Long playlistId, String email) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> playlist.getSongs().stream()
                        .limit(100)
                        .map(song -> playlistMapper.songEntityToViewModel(song, email))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ObjectNotFoundException("Playlist ID: " + playlistId + " not found"));
    }

    @Override //Update the playlist
    public void updatePlaylist(Long playlistId, PlaylistBindingModel playlistBindingModel, String email) {
        // Retrieve the existing playlist
        PlaylistEntity playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ObjectNotFoundException("Playlist not found: " + playlistId));

        // Apply updates
        playlist.setName(playlistBindingModel.getName());
        playlist.setDescription(playlistBindingModel.getDescription());
        playlist.setGenre(PlaylistGenreEnums.valueOf(String.valueOf(playlistBindingModel.getGenre())));
        playlist.setIsPrivate(playlistBindingModel.getIsPrivate());


        // Update songs in the playlist if necessary
        updateSongsInPlaylist(playlistBindingModel, playlist);


        LOGGER.info("Updating playlist {}", playlist.getName());
        playlistRepository.save(playlist);
    }

    @Override
    public PlaylistViewModel findById(Long id) {
        return playlistRepository.findById(id)
                .map(playlistMapper::playlistEntityToViewModel)
                .orElseThrow(() -> new ObjectNotFoundException("Playlist not found: " + id));
    }

    @Override
    @Transactional
    public void deletePlaylist(Long id) {
        playlistRepository.deleteById(id);
    }



    @Override
    @Transactional
    public Map<String, Integer> ratePlaylist(Long id, String email, RatingType ratingType) {
        PlaylistEntity playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Playlist not found: " + id));

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User not found: " + email));

        //Check if the user has already rated the playlist
        Optional<PlaylistRatingEntity> existingRating = playlist.getRatings()
                .stream()
                .filter(r -> r.getUser().equals(user))
                .findFirst();

        if (existingRating.isPresent()) {
            //update rating
            existingRating.get().setRatingType(ratingType);
        } else {
            //Create new rating
            PlaylistRatingEntity newRating = new PlaylistRatingEntity();
            newRating.setPlaylist(playlist);
            newRating.setUser(user);
            newRating.setRatingType(ratingType);
            playlist.getRatings().add(newRating);
        }
        playlistRepository.save(playlist);


        PlaylistEntity updatedEntity = playlistRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Playlist not found: " + id));

        int likeCount = (int) updatedEntity.getRatings()
                .stream()
                .filter( playlistRatingEntity ->
                        playlistRatingEntity.getRatingType() == RatingType.LIKE)
                .count();
        int dislikeCount = (int) updatedEntity.getRatings()
                .stream()
                .filter( playlistRatingEntity ->
                        playlistRatingEntity.getRatingType() == RatingType.DISLIKE)
                .count();

        Map<String, Integer> counts = new HashMap<>();
        counts.put("likeCount", likeCount);
        counts.put("dislikeCount", dislikeCount);

        return counts;
    }

    @Override
    public Page<PlaylistViewModel> findByLatestCreated(Pageable pageable) {

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        Page<PlaylistEntity> byDate = playlistRepository.findByIsPrivateFalseAndCreatedOnAfter(oneWeekAgo, pageable);

        return byDate.map(playlistMapper::playlistEntityToViewModel);

    }

    @Override
    @Cacheable("topRatedPlaylists")
    public List<PlaylistViewModel> topRatedPlaylists() {
        Pageable topFive = PageRequest.of(0, 4);
        System.out.println("Cache has been updated");
        List<PlaylistEntity> topRated = playlistRepository.findTopRatedPlaylists(topFive);
        return topRated.stream()
                .map(playlistMapper::playlistEntityToViewModel)
                .toList();
    }

    @Override
    public Page<PlaylistViewModel> getAll(Pageable pageable) {
        Page<PlaylistEntity> allPlaylists = playlistRepository.findAll(pageable);
        return allPlaylists.map(playlistMapper::playlistEntityToViewModel);
    }

    @Override
    public Page<PlaylistViewModel> searchPlaylists(String query, Pageable pageable) {
        Page<PlaylistEntity> bySearchQuery = playlistRepository.findBySearchQuery(query, pageable);

        return bySearchQuery.map(playlistMapper::playlistEntityToViewModel);
    }

    @Override
    public boolean isOwner(Long id, String email) {
        return isOwner(
                playlistRepository.findById(id).orElse(null),
                email
        );
    }

    private boolean isOwner(PlaylistEntity playlistEntity, String email) {
        if (playlistEntity == null || email == null) {
            // anonymous users have no playlists
            // missing playlists are meaningless
            return false;
        }

        UserEntity viewerEntity =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new ObjectNotFoundException("Unknown user with email..." + email));

        if (isAdmin(viewerEntity)) {
            // all admins own all offers
            return true;
        }

        return Objects.equals(
                playlistEntity.getUser().getId(),
                viewerEntity.getId());
    }

    private boolean isAdmin(UserEntity userEntity) {
        return userEntity
                .getRoles()
                .stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(r -> UserRoleEnum.ADMIN == r);
    }

    private void updateSongsInPlaylist(PlaylistBindingModel playlistBindingModel, PlaylistEntity playlist) {
        List<SongEntity> songList = new ArrayList<>();
        for (Long songId : playlistBindingModel.getSongIds()) {
            songRepository.findById(songId).ifPresent(song -> {
                if (!songList.contains(song)) {
                    songList.add(song);
                }
            });
        }
        playlist.setSongs(songList);
    }
}

