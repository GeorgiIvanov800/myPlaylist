package org.myplaylist.myplaylist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceImplTest {
    private PlaylistServiceImpl testService;
    @Mock
    private PlaylistRepository mockPlaylistRepository;
    @Mock
    private SongRepository mockSongRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private PlaylistMapper playlistMapper;

    @BeforeEach
    void setUp() {
        testService = new PlaylistServiceImpl(
                mockPlaylistRepository,
                mockSongRepository,
                mockUserRepository,
                playlistMapper
        );
    }

    @Test
    void testCreatePlaylist_UserFound() {
        // Arrange
        String email = "user@example.com";
        UserEntity userEntity = new UserEntity();

        PlaylistBindingModel playlistBindingModel = new PlaylistBindingModel();
        // Assuming getSongIds returns a List<Long>, initialize it to prevent NullPointerException
        playlistBindingModel.setSongIds(new ArrayList<>());

        PlaylistEntity playlistEntity = new PlaylistEntity();

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(playlistMapper.playListBindingModelToEntity(playlistBindingModel)).thenReturn(playlistEntity);

        // Act
        testService.createPlaylist(playlistBindingModel, email);

        // Assert
        assertEquals(userEntity, playlistEntity.getUser());
        assertNotNull(playlistEntity.getCreatedOn());
        verify(mockPlaylistRepository).save(playlistEntity);
    }

    @Test
    void testCreatePlaylist_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        PlaylistBindingModel playlistBindingModel = new PlaylistBindingModel();
        PlaylistEntity playlistEntity = new PlaylistEntity(); // Create a non-null PlaylistEntity

        when(playlistMapper.playListBindingModelToEntity(playlistBindingModel)).thenReturn(playlistEntity);
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> testService.createPlaylist(playlistBindingModel, email));
    }


    @Test
    void testGetUserPlaylist() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<PlaylistEntity> playlistEntities = new ArrayList<>();
        playlistEntities.add(new PlaylistEntity());
        playlistEntities.add(new PlaylistEntity());
        Page<PlaylistEntity> playlistPage = new PageImpl<>(playlistEntities, pageable, playlistEntities.size());
        when(mockPlaylistRepository.findByUserId(userId, pageable)).thenReturn(playlistPage);

        Page<PlaylistViewModel> result = testService.getUserPlaylist(pageable, userId);

        assertEquals(playlistEntities.size(), result.getContent().size());
        verify(mockPlaylistRepository).findByUserId(userId, pageable);
    }

    @Test
    void testGetTotalSongCountForUser() {
        Long userId = 1L;
        Long expectedCount = 10L;
        when(mockPlaylistRepository.countTotalSongsByUserId(userId)).thenReturn(expectedCount);

        Long result = testService.getTotalSongCountForUser(userId);

        assertEquals(expectedCount, result);
        verify(mockPlaylistRepository).countTotalSongsByUserId(userId);
    }

    @Test
    void testGetSongsForPlaylist_PlaylistFound() {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        PlaylistEntity playlistEntity = new PlaylistEntity();
        List<SongEntity> songEntities = List.of(new SongEntity(), new SongEntity());
        playlistEntity.setSongs(songEntities);

        List<SongViewModel> songViewModels = songEntities.stream()
                .map(song -> createTestViewModel())
                .toList();

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlistEntity));
        when(playlistMapper.songEntityToViewModel(any(SongEntity.class), eq(email)))
                .thenAnswer(invocation -> createTestViewModel());

        // Act
        List<SongViewModel> result = testService.getSongsForPlaylist(playlistId, email);

        // Assert
        assertEquals(songViewModels.size(), result.size());
        verify(playlistMapper, times(songEntities.size())).songEntityToViewModel(any(SongEntity.class), eq(email));
    }

    @Test
    void testGetSongsForPlaylist_PlaylistNotFound() {
        // Arrange
        Long playlistId = 99L; // Non-existent playlist ID
        String email = "user@example.com";

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> testService.getSongsForPlaylist(playlistId, email));
    }

    @Test
    void testUpdatePlaylist_Success() {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        PlaylistBindingModel playlistBindingModel = createTestPlaylistBindingModel();
        playlistBindingModel.setName("New Name");
        playlistBindingModel.setDescription("New Description");
        playlistBindingModel.setGenre(PlaylistGenreEnums.RnB);
        playlistBindingModel.setIsPrivate(true);

        PlaylistEntity playlistEntity = new PlaylistEntity();
        List<SongEntity> songs = List.of(createTestSong());
        playlistEntity.setSongs(songs);
        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlistEntity));

        // Act
        testService.updatePlaylist(playlistId, playlistBindingModel, email);

        // Assert
        assertEquals("New Name", playlistEntity.getName());
        assertEquals("New Description", playlistEntity.getDescription());
        assertEquals(PlaylistGenreEnums.RnB, playlistEntity.getGenre());
        assertTrue(playlistEntity.getIsPrivate());
        verify(mockPlaylistRepository).save(playlistEntity);

    }

    @Test
    void testUpdatePlaylist_PlaylistNotFound() {
        // Arrange
        Long playlistId = 99L; // Non-existent playlist ID
        PlaylistBindingModel playlistBindingModel = new PlaylistBindingModel();
        String email = "user@example.com";

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> testService.updatePlaylist(playlistId, playlistBindingModel, email));
    }

    @Test
    void testFindById_PlaylistFound() {
        // Arrange
        Long playlistId = 1L;
        PlaylistEntity playlistEntity = new PlaylistEntity();
        PlaylistViewModel expectedViewModel = new PlaylistViewModel();

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlistEntity));
        when(playlistMapper.playlistEntityToViewModel(playlistEntity)).thenReturn(expectedViewModel);

        // Act
        PlaylistViewModel actualViewModel = testService.findById(playlistId);

        // Assert
        assertEquals(expectedViewModel, actualViewModel);
        verify(playlistMapper).playlistEntityToViewModel(playlistEntity);
    }

    @Test
    void testFindById_PlaylistNotFound() {
        // Arrange
        Long playlistId = 99L;

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> testService.findById(playlistId));
    }

    @Test
    void testDeletePlaylist() {
        // Arrange
        Long playlistId = 1L;

        // Act
        testService.deletePlaylist(playlistId);

        // Assert
        verify(mockPlaylistRepository).deleteById(playlistId);
    }

    @Test
    void testRatePlaylist_NewRating() {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        PlaylistEntity playlist = new PlaylistEntity();
        UserEntity user = new UserEntity();
        playlist.setRatings(new HashSet<>());

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Map<String, Integer> result = testService.ratePlaylist(playlistId, email, RatingType.LIKE);

        // Assert
        assertFalse(playlist.getRatings().isEmpty());
        assertEquals(1, result.get("likeCount").intValue());
        assertEquals(0, result.get("dislikeCount").intValue());
        verify(mockPlaylistRepository, times(2)).findById(playlistId);
        verify(mockPlaylistRepository).save(playlist);
    }

    @Test
    void testRatePlaylist_UpdateRating() {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        PlaylistEntity playlist = new PlaylistEntity();
        UserEntity user = new UserEntity();
        PlaylistRatingEntity rating = new PlaylistRatingEntity();
        rating.setUser(user);
        rating.setRatingType(RatingType.LIKE);
        playlist.setRatings(new HashSet<>(Collections.singletonList(rating)));

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Map<String, Integer> result = testService.ratePlaylist(playlistId, email, RatingType.DISLIKE);

        // Assert
        assertEquals(0, result.get("likeCount").intValue());
        assertEquals(1, result.get("dislikeCount").intValue());
        verify(mockPlaylistRepository, times(2)).findById(playlistId);
        verify(mockPlaylistRepository).save(playlist);
    }

    @Test
    void testRatePlaylist_PlaylistNotFound() {
        // Arrange
        Long playlistId = 99L;
        String email = "user@example.com";

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> testService.ratePlaylist(playlistId, email, RatingType.LIKE));
    }

    @Test
    void testFindByLatestCreated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10); // Example pageable

        PlaylistEntity playlistEntity = new PlaylistEntity(); // Create a PlaylistEntity instance
        PlaylistViewModel playlistViewModel = new PlaylistViewModel(); // Create a PlaylistViewModel instance
        Page<PlaylistEntity> playlistEntityPage = new PageImpl<>(List.of(playlistEntity)); // Create a page of PlaylistEntity

        when(playlistMapper.playlistEntityToViewModel(playlistEntity)).thenReturn(playlistViewModel);
        when(mockPlaylistRepository.findByIsPrivateFalseAndCreatedOnAfter(any(LocalDateTime.class), eq(pageable)))
                .thenReturn(playlistEntityPage);

        // Act
        Page<PlaylistViewModel> result = testService.findByLatestCreated(pageable);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(playlistViewModel, result.getContent().get(0));
        verify(playlistMapper).playlistEntityToViewModel(playlistEntity);
    }

    @Test
    void testRatePlaylist_UserNotFound() {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        PlaylistEntity playlist = new PlaylistEntity();

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> testService.ratePlaylist(playlistId, email, RatingType.LIKE));
    }

//    @Test
//    void testTopRatedPlaylists() {
//        // Arrange
//        Pageable pageable = PageRequest.of(0, 10);
//
//        PlaylistEntity playlistEntity = new PlaylistEntity();
//        PlaylistViewModel playlistViewModel = new PlaylistViewModel();
//        Page<PlaylistEntity> playlistEntityPage = new PageImpl<>(List.of(playlistEntity));
//
//        when(playlistMapper.playlistEntityToViewModel(playlistEntity)).thenReturn(playlistViewModel);
//        when(mockPlaylistRepository.findTopRatedPlaylists(pageable)).thenReturn(playlistEntityPage);
//
//        // Act
//        Page<PlaylistViewModel> result = testService.topRatedPlaylists(pageable);
//
//        // Assert
//        assertFalse(result.isEmpty());
//        assertEquals(playlistViewModel, result.getContent().get(0));
//        verify(playlistMapper).playlistEntityToViewModel(playlistEntity);
//    }

    @Test
    void testIsOwner_PlaylistExists_UserIsAdmin() {
        // Arrange
        Long playlistId = 1L;
        String email = "admin@example.com";
        PlaylistEntity playlist = new PlaylistEntity();
        UserEntity adminUser = createTestUser();

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(adminUser));

        // Act
        boolean isOwner = testService.isOwner(playlistId, email);

        // Assert
        assertTrue(isOwner);
    }

    @Test
    void testIsOwner_PlaylistExists_UserIsOwner() {
        // Arrange
        Long playlistId = 1L;
        String email = "owner@example.com";
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        owner.setEmail(email);

        PlaylistEntity playlist = new PlaylistEntity();
        playlist.setUser(owner);

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(owner));

        // Act
        boolean isOwner = testService.isOwner(playlistId, email);

        // Assert
        assertTrue(isOwner);
    }

    @Test
    void testIsOwner_PlaylistExists_UserIsNotOwner() {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        UserEntity user = new UserEntity();
        user.setId(2L); // Different from owner's ID

        PlaylistEntity playlist = new PlaylistEntity();
        playlist.setUser(new UserEntity()); // Owner is a different user

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        boolean isOwner = testService.isOwner(playlistId, email);

        // Assert
        assertFalse(isOwner);
    }

    @Test
    void testIsOwner_PlaylistDoesNotExist() {
        // Arrange
        Long playlistId = 99L; // Non-existent playlist ID
        String email = "user@example.com";

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.empty());

        // Act
        boolean isOwner = testService.isOwner(playlistId, email);

        // Assert
        assertFalse(isOwner);
    }

    @Test
    void testIsOwner_UserDoesNotExist() {
        // Arrange
        Long playlistId = 1L;
        String email = "nonexistent@example.com";
        PlaylistEntity playlist = new PlaylistEntity(); // Ensure playlist is found

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ObjectNotFoundException.class, () -> testService.isOwner(playlistId, email));
    }

    private static PlaylistBindingModel createTestPlaylistBindingModel() {
        PlaylistBindingModel playlistBindingModel = new PlaylistBindingModel();

        // Set fields based on the constraints in your model
        playlistBindingModel.setName("Test Playlist");
        playlistBindingModel.setDescription("This is a test playlist description");
        playlistBindingModel.setGenre(PlaylistGenreEnums.RnB);
        playlistBindingModel.setSongIds(Arrays.asList(1L, 2L, 3L));
        playlistBindingModel.setPictureUrl("http://example.com/image.jpg");
        playlistBindingModel.setIsPrivate(false);

        return playlistBindingModel;
    }

    private static UserEntity createTestUser() {
        UserEntity testUser = new UserEntity();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("StrongPassword123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setUsername("testUser");
        testUser.setRegisterDate(LocalDateTime.now());
        testUser.setActive(false);
        List<UserRoleEntity> roles = new ArrayList<>();
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.USER));
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.ADMIN));
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.MODERATOR));
        testUser.setRoles(roles);
        return testUser;
    }

    private static SongViewModel createTestViewModel() {
        SongViewModel testViewModel = new SongViewModel(
                1L,
                "testTitle",
                "testArtis",
                "testAlbum",
                "testGenre",
                "testFile",
                Duration.ofSeconds(3),
                "formated",
                true

        );
        return testViewModel;
    }

    private static SongEntity createTestSong() {
        SongEntity testSong = new SongEntity();
        testSong.setTitle("testTitle");
        testSong.setArtist("testArtis");
        testSong.setArtist("testAlbum");
        testSong.setGenre("testGenre");
        testSong.setFilePath("testFilePath");
        testSong.setDuration(Duration.ofMinutes(3));
        testSong.setUser(createTestUser());
        return testSong;
    }

    private static PlaylistEntity createTestPlaylist() {
        PlaylistEntity testPlaylistEntity = new PlaylistEntity();
        testPlaylistEntity.setName("Test Playlist");
        testPlaylistEntity.setGenre(PlaylistGenreEnums.Disco);
        testPlaylistEntity.setPictureUrl("http://example.com/image.jpg");
        testPlaylistEntity.setDescription("This is a test playlist");
        testPlaylistEntity.setCreatedOn(LocalDateTime.now());
        testPlaylistEntity.setIsPrivate(false);

        return testPlaylistEntity;
    }


    private static UserEntity createTestNoAdminUser() {
        UserEntity testUser = new UserEntity();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("StrongPassword123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setUsername("testUser");
        testUser.setRegisterDate(LocalDateTime.now());
        testUser.setActive(false);
        List<UserRoleEntity> roles = new ArrayList<>();
        roles.add(new UserRoleEntity().setRole(UserRoleEnum.USER));
        testUser.setRoles(roles);
        return testUser;
    }
}
