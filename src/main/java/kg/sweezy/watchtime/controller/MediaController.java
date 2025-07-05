package kg.sweezy.watchtime.controller;

import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.service.MediaService;
import kg.sweezy.watchtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/subscribe")
    public String subscribeByChannelId(@RequestParam Long channelId){
        return mediaService.subscribeByChannelId(channelId);
    }

    @PostMapping("/like-video-by-id")
    public ResponseEntity<?> likeVideoById(@RequestParam Long videoId){
        mediaService.likeVideoById(videoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike-video-by-id")
    public ResponseEntity<?> dislikeVideoById(@RequestParam Long videoId){
        mediaService.dislikeVideoById(videoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-to-play-list-by-id")
    public ResponseEntity<?> addToPlayListById(@RequestParam Long videoId){
        mediaService.addToPlayList(videoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-from-play-list-by-id")
    public ResponseEntity<?> removeFromPlayListById(@RequestParam Long videoId){
        mediaService.removeFromPlayList(videoId);
        return ResponseEntity.ok().build();
    }
}
