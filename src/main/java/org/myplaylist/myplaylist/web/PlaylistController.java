package org.myplaylist.myplaylist.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/playlist")
public class PlaylistController {

    @GetMapping("/create")
    public String create() {
        return "playlist-create";
    }
}
