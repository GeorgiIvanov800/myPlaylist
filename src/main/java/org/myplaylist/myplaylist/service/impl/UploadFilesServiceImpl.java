package org.myplaylist.myplaylist.service.impl;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.myplaylist.myplaylist.model.entity.PlaylistEntity;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.model.entity.UserEntity;
import org.myplaylist.myplaylist.repository.PlaylistRepository;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.repository.UserRepository;
import org.myplaylist.myplaylist.service.UploadFilesService;
import org.myplaylist.myplaylist.service.aop.WarnIfExecutionExceeds;
import org.myplaylist.myplaylist.utils.impl.NextCloudWebDavClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class UploadFilesServiceImpl implements UploadFilesService {
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final NextCloudWebDavClient nextCloudWebDavClient;
    private final PlaylistRepository playlistRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFilesServiceImpl.class);
    private static final String YEAR_FIELD_KEY = "YEAR";
    private static final int DEFAULT_YEAR = 0;
    private static final String DEFAULT_ALBUM = "Unknown Album";
    private static final String DEFAULT_GENRE = "Unknown Genre";


    public UploadFilesServiceImpl(SongRepository songRepository,
                                  UserRepository userRepository,
                                  NextCloudWebDavClient nextCloudWebDavClient,
                                  PlaylistRepository playlistRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.nextCloudWebDavClient = nextCloudWebDavClient;
        this.playlistRepository = playlistRepository;
    }

    @WarnIfExecutionExceeds(
            timeInMillis = 5000L
    )
    @Override
    public boolean uploadSongs(String email, MultipartFile[] files) {
        //Upload the songs
        List<SongEntity> songs = new ArrayList<>();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email + " not found"));

        String formattedEmail = email.replace("@", "_at_").replace(".", "_dot_");

        List<List<MultipartFile>> fileBatches = createFileBatches(Arrays.asList(files), 5); // Batch size set to 5

        try {
            for (List<MultipartFile> batch : fileBatches) {
                for (MultipartFile file : batch) {
                    if (!isFileValid(file)) {
                        LOGGER.warn("Invalid file encountered. Aborting upload.");
                        return false;
                    }
                    String originalFileName = file.getOriginalFilename();
                    File convertedFile = convertMultipartFileToFile(file, originalFileName);
                    String remotePath = "Songs/" + originalFileName;

                    List<String> pathAndShareLink = nextCloudWebDavClient.uploadFile(convertedFile, remotePath, formattedEmail);

                    SongEntity song = processAudioFiles(convertedFile, user, originalFileName);
                    assert song != null;
                    song.setFilePath(pathAndShareLink.get(0));
                    song.setNextCloudPath(pathAndShareLink.get(1));
                    songs.add(song);

                    if (convertedFile.exists()) {
                        boolean isDeleted = convertedFile.delete();
                        if (!isDeleted) {
                            LOGGER.warn("Temporary file deletion failed for file: " + convertedFile.getAbsolutePath());
                        }
                    }

                }
                songRepository.saveAll(songs);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        LOGGER.info("Successfully uploaded files {}", fileBatches.size());
        return true;
    }
    @WarnIfExecutionExceeds(
            timeInMillis = 2000L
    )
    @Override
    public boolean uploadPlaylistImage(Long playlistId, MultipartFile imageFile, String email) {

        String formattedEmail = email.replace("@", "_at_").replace(".", "_dot_");
        String originalFileName = imageFile.getOriginalFilename();
        String remotePath = "PlaylistImages/" + playlistId + "/" + originalFileName;

        try {
            File convertedFile = convertMultipartFileToFile(imageFile, originalFileName);
            List<String> pathAndShareLink = nextCloudWebDavClient.uploadFile(convertedFile, remotePath, formattedEmail);

            // Update the playlist entity with the new image path
            PlaylistEntity playlist = playlistRepository.findById(playlistId)
                    .orElseThrow(() -> new IllegalArgumentException("Playlist with id:" + playlistId + " not found"));
            playlist.setPictureUrl(pathAndShareLink.get(0));
            playlistRepository.save(playlist);

            if (convertedFile.exists()) {
                boolean isDeleted = convertedFile.delete();
                if (!isDeleted) {
                    LOGGER.warn("Temporary file deletion failed for file: " + convertedFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error uploading playlist image: " + e.getMessage());
            return false;
        }

        LOGGER.info("Successfully uploaded playlist image for playlist {}", playlistId);
        return true;
    }

    private SongEntity processAudioFiles(File file, UserEntity user, String filename) {

        String extension = filename != null ? filename.substring(filename.lastIndexOf('.')) : "";

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            Integer year = DEFAULT_YEAR;
            String album = DEFAULT_ALBUM;
            String genre = DEFAULT_GENRE;

            if (tag != null) {
                String yearFieldString = tag.getFirst(YEAR_FIELD_KEY);
                year = parseYearField(yearFieldString);

                album = tag.getFirst(FieldKey.ALBUM) != null ? tag.getFirst(FieldKey.ALBUM) : DEFAULT_ALBUM;
                genre = tag.getFirst(FieldKey.GENRE) != null ? tag.getFirst(FieldKey.GENRE) : DEFAULT_GENRE;
            }

            SongEntity song = new SongEntity();
            assert filename != null;
            song.setArtist(filename.substring(0, filename.lastIndexOf('.')));
            song.setAlbum(album);
            song.setYear(year);
            song.setGenre(genre);
            song.setDuration(Duration.ofSeconds(audioFile.getAudioHeader().getTrackLength()));
            song.setUser(user);

            song.setType(extension);

            return song;

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
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

    private boolean isFileValid(MultipartFile file) {
        return Objects.equals(file.getContentType(), "audio/mpeg")
                && !file.isEmpty()
                && file.getSize() <= 30 * 1024 * 1024; // 30MB limit
    }

    private List<List<MultipartFile>> createFileBatches(List<MultipartFile> files, int batchSize) {
        List<List<MultipartFile>> batches = new ArrayList<>();
        for (int start = 0; start < files.size(); start += batchSize) {
            int end = Math.min(start + batchSize, files.size());
            batches.add(new ArrayList<>(files.subList(start, end)));
        }
        return batches;
    }

    private Integer parseYearField(String yearFieldString) {
        try {
            return Integer.parseInt(yearFieldString);
        } catch (NumberFormatException e) {
            return DEFAULT_YEAR;
        }
    }
}
