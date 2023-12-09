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
                                size = 5,
                                sort = "createdOn",
                                direction = Sort.Direction.DESC
                        ) Pageable pageable) {

        Page<PlaylistViewModel> latestCreatedPlaylists = playlistService.findByLatestCreated(pageable);

        Pageable topRatedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<PlaylistViewModel> topRatedPlaylists = playlistService.topRatedPlaylists(topRatedPageable);
        model.addAttribute("playlist", latestCreatedPlaylists);
        model.addAttribute("topRated", topRatedPlaylists);

        return "discover";
    }

}
