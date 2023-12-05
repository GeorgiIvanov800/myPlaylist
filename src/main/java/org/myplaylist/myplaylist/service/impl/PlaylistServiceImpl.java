package org.myplaylist.myplaylist.service.impl;

import jakarta.transaction.Transactional;
import org.myplaylist.myplaylist.config.PlaylistMapper;
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
import org.springframework.scheduling.annotation.Scheduled;
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
    private static final String UPLOAD_DIR = "/home/givanov/IdeaProjects/myplaylist/src/main/resources/static/playlist-images/";

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository, UserRepository userRepository, PlaylistMapper playlistMapper) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.playlistMapper = playlistMapper;
    }

    @Override
    public void createPlaylist(PlaylistBindingModel playlistBindingModel, String email) {

//        PlaylistEntity playlist = new PlaylistEntity();

        System.out.println();

        PlaylistEntity playlist = playlistMapper.playListBindingModelToEntity(playlistBindingModel);

//        playlist.setName(playlistBindingModel.getName());
//        playlist.setDescription(playlistBindingModel.getDescription());
//        playlist.setGenre(PlaylistGenreEnums.valueOf(String.valueOf(playlistBindingModel.getGenre())));
        playlist.setCreatedOn(LocalDateTime.now());
        playlist.setIsPrivate(playlistBindingModel.getIsPrivate());


        Optional<UserEntity> userOptional = userRepository.findByEmail(email); // Or I should use the service
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

        Page<PlaylistEntity> byUser = playlistRepository.findByUserId(userId, pageable);
        return byUser.map(playlistMapper::playlistEntityToViewModel);
    }

    @Override
    public Long getTotalSongCountForUser(Long userId) {
        return playlistRepository.countTotalSongsByUserId(userId);
    }

    @Override
    public void updatePlaylistImage(Long playlistId, String pictureUrl, MultipartFile pictureFile, String filename) throws IOException {

        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = pictureFile.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Log error and/or rethrow as custom exception
            throw new IllegalArgumentException("Error saving file: " + filename, e);
        }


        PlaylistEntity playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid playlist ID: " + playlistId));
        playlist.setPictureUrl(pictureUrl);
        playlistRepository.save(playlist);

    }

    @Override
    public List<SongViewModel> getSongsForPlaylist(Long playlistId, String email) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> playlist.getSongs().stream()
                        .limit(100)
                        .map(song -> playlistMapper.songEntityToViewModel(song, email))
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ObjectNotFoundException("Playlist ID: " + playlistId + " not found"));
    }

    @Override
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

        //TODO:check if the user updating the playlist is the owner

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
    public boolean isOwner(Long id, String email) {
        return isOwner(
                playlistRepository.findById(id).orElse(null),
                email
        );
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
    public Page<PlaylistViewModel> topRatedPlaylists(Pageable pageable) {

        pageable = PageRequest.of(0, 10);

        Page<PlaylistEntity> topRated = playlistRepository.findTopRatedPlaylists(pageable);
        return topRated.map(playlistMapper::playlistEntityToViewModel);
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

