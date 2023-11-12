package org.myplaylist.myplaylist.init;

import jakarta.transaction.Transactional;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.myplaylist.myplaylist.model.entity.SongEntity;
import org.myplaylist.myplaylist.repository.SongRepository;
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
    private static final String path = "/mnt/938cfd0c-3042-4e00-a6f3-eaa7cbc2a4ad/Music";

    private final int BATCH_SIZE = 100;

    public MusicLibraryFeed(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public void feedDatabase() {

        List<SongEntity> songsBatch = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            List<File> files = paths.filter(Files::isRegularFile)
                    .filter(this::isSupportedAudioFile)
                    .map(Path::toFile)
                    .toList();

            for (File file : files) {
                SongEntity song = processAudioFiles(file);
                if (song != null) {
                    songsBatch.add(song);
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

    @Transactional
    private void saveAndClearBatch(List<SongEntity> songsBatch) {
        songRepository.saveAll(songsBatch);
        songsBatch.clear(); // Clear the batch list to free memory
    }

    private SongEntity processAudioFiles(File file) {
        try {
            if (isSupportedAudioFile(file.toPath())) {

                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();

                SongEntity song = new SongEntity();

                song.setTitle(tag.getFirst(FieldKey.TITLE));
                song.setArtist(tag.getFirst(FieldKey.ARTIST));
                song.setAlbum(tag.getFirst(FieldKey.ALBUM));
                song.setYear(tag.getFirst(FieldKey.YEAR) != null ? Integer.parseInt(tag.getFirst(FieldKey.YEAR)) : null);
                song.setGenre(tag.getFirst(FieldKey.GENRE));
                song.setDuration(Duration.ofSeconds(audioFile.getAudioHeader().getTrackLength()));
                song.setType(file.getPath());

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

    @Override
    public void run(String... args) throws Exception {
//        feedDatabase();
    }
}

