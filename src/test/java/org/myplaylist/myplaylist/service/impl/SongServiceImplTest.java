package org.myplaylist.myplaylist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myplaylist.myplaylist.config.mapper.PlaylistMapper;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.PlaylistGenreEnums;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.model.view.SongViewModel;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.utils.impl.NextCloudWebDavClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SongServiceImplTest {

    private SongServiceImpl serviceToTest;
    @Mock
    private SongRepository mockSongRepository;
    @Mock
    private  PlaylistMapper mockPlaylistMapper;
    @Mock
    private  UserRepository mockUserRepository;
    @Mock
    private  PlaylistRepository mockPlaylistRepository;
    @Mock
    private  NextCloudWebDavClient mockNextCloudWebDavClient;

    @BeforeEach
    void setUp() {
        serviceToTest = new SongServiceImpl(
                mockSongRepository,
                mockPlaylistMapper,
                mockUserRepository,
                mockPlaylistRepository,
                mockNextCloudWebDavClient
        );
    }

    @Test
    void getAllSongs_ReturnsCorrectData() {
        // Arrange
        List<SongEntity> mockSongEntities = Arrays.asList(createTestSong(), createTestSong());

        when(mockSongRepository.findAllByUserIsNull()).thenReturn(mockSongEntities);

        // Mock the mapper's behavior
        when(mockPlaylistMapper.songEntityToViewModelWithoutOwner(any(SongEntity.class)))
                .thenAnswer(invocation -> {
                    SongEntity song = invocation.getArgument(0);
                    String formattedDuration = formatDuration(song.getDuration());
                    boolean userIsOwner = song.getUser().getEmail().equals("testuser@example.com");

                    return new SongViewModel(song.getId(), song.getTitle(), song.getArtist(),
                            song.getAlbum(), song.getGenre(), song.getFilePath(),
                            song.getDuration(), formattedDuration, userIsOwner);
                });

        // Act
        List<SongViewModel> result = serviceToTest.getAllSongs();

        // Assert
        assertNotNull(result);
        assertEquals(mockSongEntities.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            SongViewModel viewModel = result.get(i);
            SongEntity entity = mockSongEntities.get(i);

            assertEquals(entity.getTitle(), viewModel.title());
            assertEquals(entity.getArtist(), viewModel.artist());

        }

        // Verify that the mapper is called for each SongEntity
        mockSongEntities.forEach(songEntity ->
                verify(mockPlaylistMapper).songEntityToViewModelWithoutOwner(songEntity)
        );
    }

    @Test
    void getUserSongs_ReturnsCorrectData() {
        // Arrange
        String userEmail = "testuser@example.com";
        List<SongEntity> mockUserSongs = Arrays.asList(createTestSong(), createTestSong());

        when(mockSongRepository.findAllByUser_Email(userEmail)).thenReturn(mockUserSongs);

        // Mock the mapper's behavior
        when(mockPlaylistMapper.songEntityToViewModel(any(SongEntity.class), eq(userEmail)))
                .thenAnswer(invocation -> {
                    SongEntity song = invocation.getArgument(0);
                    String formattedDuration = formatDuration(song.getDuration());
                    boolean userIsOwner = song.getUser().getEmail().equals("testuser@example.com");
                    return new SongViewModel(song.getId(), song.getTitle(), song.getArtist(),
                            song.getAlbum(), song.getGenre(), song.getFilePath(),
                            song.getDuration(), formattedDuration, userIsOwner);
                });

        // Act
        List<SongViewModel> result = serviceToTest.getUserSongs(userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(mockUserSongs.size(), result.size());


        // Verify that the mapper is called for each SongEntity
        mockUserSongs.forEach(songEntity ->
                verify(mockPlaylistMapper).songEntityToViewModel(songEntity, userEmail)
        );
    }

    @Test
    void deleteSong_SongExists_DeletesSongAndUpdatesPlaylists() throws Exception {
        // Arrange
        Long songId = 1L;
        SongEntity songToDelete = createTestSong();
        songToDelete.setPlaylists(new HashSet<>(Arrays.asList(
                createTestPlaylist(),
                createTestPlaylist()
        )));

        when(mockSongRepository.findById(songId)).thenReturn(Optional.of(songToDelete));

        // Act
        serviceToTest.deleteSong(songId);

        // Assert
        for (PlaylistEntity playlist : songToDelete.getPlaylists()) {
            if (playlist.getSongs().isEmpty()) {
                verify(mockPlaylistRepository).delete(playlist);
            } else {
                verify(mockPlaylistRepository).save(playlist);
            }
        }
        verify(mockNextCloudWebDavClient).deleteFile(songToDelete.getNextCloudPath());
        verify(mockSongRepository).deleteById(songId);
    }


    @Test
    void deleteSong_SongDoesNotExist_ThrowsException() {
        // Arrange
        Long invalidSongId = 2L;
        when(mockSongRepository.findById(invalidSongId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            // Act
            serviceToTest.deleteSong(invalidSongId);
        });
    }
    @Test
    void testIsOwner_UserIsOwner() {
        // Arrange
        Long songId = 1L;
        String userEmail = "user@example.com";
        Long userId = 2L;

        SongEntity mockSong = new SongEntity();
        UserEntity mockUser = new UserEntity();
        UserEntity mockSongOwner = new UserEntity();

        mockUser.setId(userId);
        mockSongOwner.setId(userId);
        mockSong.setUser(mockSongOwner);

        when(mockSongRepository.findById(songId)).thenReturn(Optional.of(mockSong));
        when(mockUserRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));

        // Act
        boolean isOwner = serviceToTest.isOwner(songId, userEmail);

        // Assert
        assertTrue(isOwner);
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

    private String formatDuration(Duration duration) {
        // Implement formatting logic similar to your application's logic.
        long hours = duration.toHours();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    private static PlaylistEntity createTestPlaylist() {
        PlaylistEntity testPlaylist = new PlaylistEntity();
        testPlaylist.setName("Test Playlist");
        testPlaylist.setGenre(PlaylistGenreEnums.Disco);
        testPlaylist.setPictureUrl("http://example.com/image.jpg");
        testPlaylist.setDescription("This is a test playlist");
        testPlaylist.setCreatedOn(LocalDateTime.now());
        testPlaylist.setIsPrivate(false);
        List<SongEntity> testSongs = new ArrayList<>();
        testSongs.add(createTestSong());
        testPlaylist.setSongs(testSongs);
        return testPlaylist;
    }

}
