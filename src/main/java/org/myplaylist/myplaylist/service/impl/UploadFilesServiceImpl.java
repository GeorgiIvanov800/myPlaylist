package org.myplaylist.myplaylist.service.impl;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.service.UploadFilesService;
import org.myplaylist.myplaylist.utils.NextCloudWebDavClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UploadFilesServiceImpl implements UploadFilesService {
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final NextCloudWebDavClient nextCloudWebDavClient;


    public UploadFilesServiceImpl(SongRepository songRepository, UserRepository userRepository, NextCloudWebDavClient nextCloudWebDavClient) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.nextCloudWebDavClient = nextCloudWebDavClient;
    }


    @Override
    public boolean upload(String email, MultipartFile[] files) {
//        TODO: IMPLEMENT AOP to check how fast is the upload doing
        //Upload the songs
        List<SongEntity> songs = new ArrayList<>();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email + " not found"));

        try {

            for (MultipartFile file : files) {
                if (Objects.equals(file.getContentType(), "audio/mpeg")
                        && !file.isEmpty()
                        && file.getSize() <= 30 * 1024 * 1024) { // 30mb limit my hardware is poor

                    String fileName = file.getOriginalFilename();
                    assert fileName != null;
                    File convertedFile = convertMultipartFileToFile(file, fileName); // Convert to File
                    String remotePath = "Songs/" + fileName; // Define the path on Nextcloud
                    nextCloudWebDavClient.uploadFile(convertedFile, remotePath); // Upload to Nextcloud

                    SongEntity song = processAudioFiles(file, user); // save the meta-info in the DB
                    assert song != null;
                    song.setFilePath(remotePath);
                    songs.add(song);
                }
                songRepository.saveAll(songs);
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    private SongEntity processAudioFiles(MultipartFile multiPartFile, UserEntity user) {

        File file;
        String originalFileName = multiPartFile.getOriginalFilename();
        String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf('.')) : "";

        try {
            file = convertMultipartFileToFile(multiPartFile, extension);
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            Integer year = null;
            int defaultYear = 0;
            String title = tag.getFirst(FieldKey.TITLE);

            SongEntity song = new SongEntity();
            String artist = tag.getFirst(FieldKey.ARTIST);

            if (artist.trim().isEmpty() && title.trim().isEmpty()) {
                artist = originalFileName;
            }
            song.setArtist(artist);

            if (tag.getFirst(FieldKey.YEAR) != null) {
                try {
                    year = Integer.parseInt(tag.getFirst(FieldKey.YEAR));
                } catch (NumberFormatException e) {
                    year = defaultYear; // replace defaultYear with default value
                }
            }
            song.setAlbum(tag.getFirst(FieldKey.ALBUM));
            song.setYear(year);
            song.setTitle(tag.getFirst(FieldKey.TITLE));
            song.setGenre(tag.getFirst(FieldKey.GENRE));
            song.setDuration(Duration.ofSeconds(audioFile.getAudioHeader().getTrackLength()));
            song.setUser(user);

            song.setType(extension);

            return song;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    //Convert MultipartFile to File
    private File convertMultipartFileToFile(MultipartFile multipartFile, String extension) throws IOException {

        // Create a temp file with a generic extension
        File tempFile = File.createTempFile("upload-", extension);

        // Write the content of the MultipartFile to the temp file
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }

        return tempFile;
    }
}
