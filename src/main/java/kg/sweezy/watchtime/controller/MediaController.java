package kg.sweezy.watchtime.controller;

import kg.sweezy.watchtime.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final VideoService videoService;

    @Autowired
    public MediaController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/like-video-by-id")
    public ResponseEntity<?> likeVideoById(@RequestParam Long videoId){
        videoService.likeMediaById(videoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike-video-by-id")
    public ResponseEntity<?> dislikeVideoById(@RequestParam Long videoId){
        videoService.dislikeMediaById(videoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-to-play-list-by-id")
    public ResponseEntity<?> addToPlayListById(@RequestParam Long videoId){
        videoService.addToPlayList(videoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove-from-play-list-by-id")
    public ResponseEntity<?> removeFromPlayListById(@RequestParam Long videoId){
        videoService.removeFromPlayList(videoId);
        return ResponseEntity.ok().build();
    }
}
