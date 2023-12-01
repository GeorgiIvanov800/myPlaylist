package org.myplaylist.myplaylist.init;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.repository.SongRepository;
import org.myplaylist.myplaylist.utils.NextCloudWebDavClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
@Component
public class MusicLibraryFeed implements CommandLineRunner {
    private final SongRepository songRepository;
    private static final String localPath = "/home/givanov/Music/AvailableSongs";

    private final NextCloudWebDavClient nextCloudWebDavClient;

    public MusicLibraryFeed(SongRepository songRepository, NextCloudWebDavClient nextCloudWebDavClient) {
        this.songRepository = songRepository;
        this.nextCloudWebDavClient = nextCloudWebDavClient;
    }

    public void feedDatabase() {

        List<SongEntity> songsBatch = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(localPath))) {
            List<File> files = paths.filter(Files::isRegularFile)
                    .filter(this::isSupportedAudioFile)
                    .map(Path::toFile)
                    .toList();

            for (File file : files) {
                SongEntity song = processAudioFiles(file);
                if (song != null) {
                    songsBatch.add(song);
                    int BATCH_SIZE = 100;
                    if (songsBatch.size() == BATCH_SIZE) {
                        saveAndClearBatch(songsBatch);
                    }
                }
            }

            // Save any remaining songs that didn't make up a full batch
            if (!songsBatch.isEmpty()) {
                saveAndClearBatch(songsBatch);
            }

        } catch (IOException e) {
            // Handle the situation when the directory cannot be accessed
            e.printStackTrace();
        }
    }


    public void saveAndClearBatch(List<SongEntity> songsBatch) {
        songRepository.saveAll(songsBatch);
        songsBatch.clear(); // Clear the batch list to free memory
    }

    private SongEntity processAudioFiles(File file) {
        try {
            if (isSupportedAudioFile(file.toPath())) {

                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();
                Integer year = null;
                int defaultYear = 0;
                SongEntity song = new SongEntity();
                String artist = tag.getFirst(FieldKey.ARTIST);

                if (artist == null || artist.trim().isEmpty()) {
                    artist = "Unknown Artist";
                }
                song.setArtist(artist);

                if (tag.getFirst(FieldKey.YEAR) != null) {
                    try {
                        year = Integer.parseInt(tag.getFirst(FieldKey.YEAR));
                    } catch (NumberFormatException e) {
                        year = defaultYear; // replace defaultYear with default value
                    }
                }

                song.setTitle(tag.getFirst(FieldKey.TITLE));
                song.setAlbum(tag.getFirst(FieldKey.ALBUM));
                song.setYear(year);
                song.setGenre(tag.getFirst(FieldKey.GENRE));
                song.setDuration(Duration.ofSeconds(audioFile.getAudioHeader().getTrackLength()));
                String localFilePath = file.getAbsolutePath();

                String nextcloudFilePath = convertLocalPathToNextcloudPath(localFilePath);
                String nextCloudUrl = nextCloudWebDavClient.createShareLink(nextcloudFilePath);
                song.setFilePath(nextCloudUrl);
                String fileType = getFileExtension(file);
                song.setType(fileType);

                return song;
                // todo
            } else {
                //todo
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    private boolean isSupportedAudioFile(Path path) {
        String fileName = path.toString();
        List<String> supportedExtensions = Arrays.asList(".mp3", ".mp4", ".ogg", ".flac");
        return supportedExtensions.stream().anyMatch(fileName::endsWith);
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

    private String convertLocalPathToNextcloudPath(String localFilePath) {
        String localBaseDirectory = localPath; // Your local base directory
        String nextcloudBaseDirectory = "AvailableSongs"; // The corresponding directory in Nextcloud

        // Ensure that the local file path starts with the local base directory
        if (!localFilePath.startsWith(localBaseDirectory)) {
            throw new IllegalArgumentException("File path does not start with the base local directory.");
        }

        // Remove the local base directory part
        String relativePath = localFilePath.substring(localBaseDirectory.length());

        // Construct the Nextcloud path
        return nextcloudBaseDirectory + relativePath;
    }

    public String generateNextcloudUrl(String localFilePath) {
        String nextcloudBasePath = "http://192.168.0.204/remote.php/dav/files/admin/";
        String relativePathInNextcloud = convertLocalPathToNextcloudPath(localFilePath);
        return nextcloudBasePath + relativePathInNextcloud;
    }


    @Override
    public void run(String... args) throws Exception {
        feedDatabase();
//        nextCloudWebDavClient.generateURL("AvailableSongs/");
    }
}

