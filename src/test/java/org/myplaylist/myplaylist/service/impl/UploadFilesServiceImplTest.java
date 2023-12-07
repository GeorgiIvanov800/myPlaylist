package org.myplaylist.myplaylist.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.utils.impl.NextCloudWebDavClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        MultipartFile file = mock(MultipartFile.class);
        MultipartFile[] files = new MultipartFile[]{file};
        UserEntity userEntity = new UserEntity();
        List<SongEntity> songs = new ArrayList<>();



        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        lenient().when(file.getOriginalFilename()).thenReturn("song.mp3");
        lenient().when(file.isEmpty()).thenReturn(false);
        lenient().when(mockNextCloudWebDavClient.uploadFile(any(File.class),
                anyString(), anyString())).thenReturn(List.of("path", "shareLink"));

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

}
