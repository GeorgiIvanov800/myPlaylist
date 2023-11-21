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

    private static final String UPLOAD_DIR = "/home/givanov/IdeaProjects/myplaylist/src/main/resources/static/uploads/";

    public UploadFilesServiceImpl(SongRepository songRepository, UserRepository userRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }


    @Override
    public boolean upload(String email, MultipartFile[] files) {
        //Upload the songs
        List<SongEntity> songs = new ArrayList<>();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email + " not found"));

        try {
            Path uploadDirectory = Paths.get(UPLOAD_DIR + email);

            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }

            for (MultipartFile file : files) {
                System.out.println();
                if (Objects.equals(file.getContentType(), "audio/mpeg")
                        && !file.isEmpty()
                        && file.getSize() <= 10 * 1024 * 1024) { // 10mb limit my hardware is poor
                    String fileName = file.getOriginalFilename();
                    assert fileName != null;
                    Path filePath = uploadDirectory.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    SongEntity song = processAudioFiles(file, user); // save the meta-info in the DB
                    assert song != null;
                    song.setFilePath(String.valueOf(filePath));
                    songs.add(song);
                }
                songRepository.saveAll(songs);
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot > 0) {
            return name.substring(lastIndexOfDot + 1).toLowerCase();
        } else {
            return ""; // No extension found
        }
    }

    private SongEntity processAudioFiles(MultipartFile multiPartFile, UserEntity user) {

        File file = null;
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
