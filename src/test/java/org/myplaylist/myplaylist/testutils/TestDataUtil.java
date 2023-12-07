package org.myplaylist.myplaylist.testutils;

import org.checkerframework.checker.units.qual.A;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.model.entity.UserRoleEntity;
import org.myplaylist.myplaylist.model.enums.PlaylistGenreEnums;
import org.myplaylist.myplaylist.model.enums.UserRoleEnum;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class TestDataUtil {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    public PlaylistEntity createTestPlaylist(UserEntity owner) {

        //create test song
        SongEntity song = new SongEntity();

        //create test playlist
        PlaylistEntity playlist = new PlaylistEntity();

        playlist.setName("Test Playlist");
        playlist.setGenre(PlaylistGenreEnums.Disco);
        playlist.setPictureUrl("http://example.com/image.jpg");
        playlist.setDescription("This is a test playlist");
        playlist.setCreatedOn(LocalDateTime.now());
        playlist.setIsPrivate(false);

        //create test song
        SongEntity testSong = createSongEntity();
        songRepository.save(testSong);

        List<SongEntity> songs = new ArrayList<>();
        songs.add(testSong);
        playlist.setSongs(songs);

        // create test user
        owner = createTestUser();
        playlist.setUser(owner);


        return playlistRepository.save(playlist);
    }

    public void cleanUp() {
        songRepository.deleteAll();
        playlistRepository.deleteAll();
    }



    private  UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setEmail("testuser@example.com");
        user.setPassword("StrongPassword123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testUser");
        user.setRegisterDate(LocalDateTime.now());
        user.setRoles(List.of(
                new UserRoleEntity().setRole(UserRoleEnum.USER),
                new UserRoleEntity().setRole(UserRoleEnum.ADMIN),
                new UserRoleEntity().setRole(UserRoleEnum.MODERATOR)
        ));
        user.setActive(false);
        return user;
    }

    private  SongEntity createSongEntity() {
        SongEntity testSong = new SongEntity();

        testSong.setTitle("Test Song");
        testSong.setArtist("Test Artist");
        testSong.setAlbum("Test Album");
        testSong.setComment("This is a test comment");
        testSong.setGenre("Pop");
        testSong.setYear(2020);
        testSong.setDuration(Duration.ofMinutes(3));
        testSong.setType("MP3");
        testSong.setNextCloudPath("http://example.com/nextcloud/testsong.mp3");
        testSong.setFilePath("/music/testsong.mp3");

        UserEntity user = createTestUser();
        testSong.setUser(user);


        return testSong;
    }
}
