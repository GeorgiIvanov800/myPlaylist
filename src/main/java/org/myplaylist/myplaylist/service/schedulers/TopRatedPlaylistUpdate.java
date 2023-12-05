package org.myplaylist.myplaylist.service.schedulers;

import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TopRatedPlaylistUpdate {

    private final PlaylistServiceImpl playlistService;

    private final CacheManager cacheManager;


    public TopRatedPlaylistUpdate(PlaylistServiceImpl playlistService, CacheManager cacheManager) {
        this.playlistService = playlistService;
        this.cacheManager = cacheManager;
    }
    @Scheduled(cron = "0 */12 * * *")
    public void updateTopRated() {

        // Clear the cache
        cacheManager.getCache("topRatedPlaylists").clear();

        // Fetch and cache new data
        Pageable pageable = PageRequest.of(0, 10);
        playlistService.topRatedPlaylists(pageable);

    }

}
