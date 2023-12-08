package org.myplaylist.myplaylist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.utils.impl.NextCloudWebDavClient;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadFilesServiceImplTest {

    private UploadFilesServiceImpl serviceToTest;
    @Mock
    private  SongRepository mockSongRepository;
    @Mock
    private  UserRepository mockUserRepository;
    @Mock
    private  NextCloudWebDavClient mockNextCloudWebDavClient;
    @Mock
    private  PlaylistRepository mockPlaylistRepository;

    @BeforeEach
    void setUp() {
        serviceToTest = new UploadFilesServiceImpl(
                mockSongRepository,
                mockUserRepository,
                mockNextCloudWebDavClient,
                mockPlaylistRepository
        );
    }

    @Test
    void uploadSongs_Success() throws Exception {
        // Arrange
        String email = "user@example.com";
        Path path = Paths.get("src/test/resources/ATB - Don't Stop.mp3");
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile file = new MockMultipartFile("file", "test-audio.mp3", "audio/mpeg", content);
        MultipartFile[] files = new MultipartFile[]{file};
        UserEntity userEntity = new UserEntity();

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        lenient().when(mockNextCloudWebDavClient.uploadFile(any(File.class), anyString(), anyString()))
                .thenReturn(List.of("path", "shareLink"));

        // Act
        boolean result = serviceToTest.uploadSongs(email, files);

        // Assert
        assertTrue(result);
        verify(mockSongRepository).saveAll(anyList());
    }

    @Test
    void uploadSongs_UserNotFound() {
        // Arrange
        String email = "test@example.com";
        MultipartFile[] files = new MultipartFile[]{};

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            // Act
            serviceToTest.uploadSongs(email, files);
        });
    }

    @Test
    void uploadSongs_InvalidFiles() throws Exception {
        // Arrange
        String email = "user@example.com";
        MultipartFile invalidFile = mock(MultipartFile.class);
        MultipartFile[] files = new MultipartFile[]{invalidFile};
        UserEntity userEntity = new UserEntity();

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(invalidFile.getContentType()).thenReturn("audio/mpeg");
        when(invalidFile.isEmpty()).thenReturn(false);
        when(invalidFile.getSize()).thenReturn(31L * 1024 * 1024);

        // Act
        boolean result = serviceToTest.uploadSongs(email, files);

        // Assert
        assertFalse(result);
        verify(mockSongRepository, never()).saveAll(anyList());
    }

    @Test
    void uploadSongs_UploadFailure() throws Exception {
        // Arrange
        String email = "user@example.com";
        MultipartFile file = mock(MultipartFile.class);
        MultipartFile[] files = new MultipartFile[]{file};
        UserEntity userEntity = new UserEntity();


        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(file.getOriginalFilename()).thenReturn("song.mp3");
        when(file.getContentType()).thenReturn("audio/mpeg");
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(10L * 1024 * 1024);

        when(file.getBytes()).thenReturn(new byte[1024]);


        doThrow(new RuntimeException("Upload failed")).when(mockNextCloudWebDavClient).uploadFile(any(File.class), anyString(), anyString());

        // Act
        assertFalse(serviceToTest.uploadSongs(email, files));
        verify(mockSongRepository, never()).saveAll(anyList());
    }

    @Test
    void uploadPlaylistImage_Success() throws Exception {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        MultipartFile imageFile = mock(MultipartFile.class);
        PlaylistEntity playlist = new PlaylistEntity();
        File convertedFile = mock(File.class);

        when(imageFile.getOriginalFilename()).thenReturn("image.jpg");
        when(imageFile.getBytes()).thenReturn(new byte[10]);
        lenient().when(convertedFile.exists()).thenReturn(true);
        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(mockNextCloudWebDavClient.uploadFile(any(File.class), anyString(), anyString())).thenReturn(List.of("imagePath", "shareLink"));

        // Act
        boolean result = serviceToTest.uploadPlaylistImage(playlistId, imageFile, email);

        // Assert
        assertTrue(result);
        assertEquals("imagePath", playlist.getPictureUrl());
        verify(mockPlaylistRepository).save(playlist);
    }

    @Test
    void uploadPlaylistImage_PlaylistNotFound() throws IOException {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        MultipartFile imageFile = mock(MultipartFile.class);

        when(mockPlaylistRepository.findById(playlistId)).thenReturn(Optional.empty());
        when(imageFile.getBytes()).thenReturn(new byte[10]);

        // Act
        boolean result = serviceToTest.uploadPlaylistImage(playlistId, imageFile, email);

        //Assert
        assertFalse(result);
    }

    @Test
    void uploadPlaylistImage_UploadFailure() throws Exception {
        // Arrange
        Long playlistId = 1L;
        String email = "user@example.com";
        MultipartFile imageFile = mock(MultipartFile.class);
        File convertedFile = mock(File.class);

        when(imageFile.getOriginalFilename()).thenReturn("image.jpg");
        when(imageFile.getBytes()).thenReturn(new byte[10]);
        doThrow(new RuntimeException("Upload failed")).when(mockNextCloudWebDavClient).uploadFile(any(File.class), anyString(), anyString());

        // Act & Assert
        assertFalse(serviceToTest.uploadPlaylistImage(playlistId, imageFile, email));
    }



}
