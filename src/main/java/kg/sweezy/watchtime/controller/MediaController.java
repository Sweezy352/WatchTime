package kg.sweezy.watchtime.controller;

import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.service.MediaService;
import kg.sweezy.watchtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/api/subscribe")
    public String subscribeByChannelId(@RequestParam Long channelId){
        return mediaService.subscribeByChannelId(channelId);
    }
}
