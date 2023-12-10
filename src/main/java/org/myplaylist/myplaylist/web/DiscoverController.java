package org.myplaylist.myplaylist.web;

import org.myplaylist.myplaylist.model.view.PlaylistViewModel;
import org.myplaylist.myplaylist.service.impl.PlaylistServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/discover")
public class DiscoverController {
    private final PlaylistServiceImpl playlistService;

    public DiscoverController(PlaylistServiceImpl playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping()
    public String discover(Model model,
                        @PageableDefault(
                                size = 4,
                                sort = "createdOn",
                                direction = Sort.Direction.DESC
                        ) Pageable pageable) {

        Page<PlaylistViewModel> latestCreatedPlaylists = playlistService.findByLatestCreated(pageable);


        List<PlaylistViewModel> topRatedPlaylists = playlistService.topRatedPlaylists();
        model.addAttribute("playlist", latestCreatedPlaylists);
        model.addAttribute("topRated", topRatedPlaylists);

        return "discover";
    }

    @GetMapping("/more")
    public String allPlaylists(Model model,
                               @PageableDefault(
                                       size = 8
                               ) Pageable pageable) {

        Page<PlaylistViewModel> playlists = playlistService.getAll(pageable);
        model.addAttribute("playlist", playlists);

        return "discover-more";
    }

    @GetMapping("/search")
    public String searchPlaylists(@RequestParam String query,
                                  Model model,
                                  @PageableDefault(size = 8)
                                      Pageable pageable) {

        Page<PlaylistViewModel> searchResults = playlistService.searchPlaylists(query, pageable);
        model.addAttribute("playlist", searchResults);
        model.addAttribute("query", query);
        return "discover-more";
    }

}
