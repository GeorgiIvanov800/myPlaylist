package org.myplaylist.myplaylist.service.schedulers;

import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TopRatedPlaylistUpdate {

    private final PlaylistServiceImpl playlistService;

    private final CacheManager cacheManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(TopRatedPlaylistUpdate.class);



    public TopRatedPlaylistUpdate(PlaylistServiceImpl playlistService, CacheManager cacheManager) {
        this.playlistService = playlistService;
        this.cacheManager = cacheManager;
    }
    @Scheduled(cron = "0 * * * * ?")
    public void updateTopRated() {

        // Clear the cache
        cacheManager.getCache("topRatedPlaylists").clear();

        // Fetch and cache new data
        playlistService.topRatedPlaylists();
        LOGGER.info("Cache for top rated has been updated");
    }

}
