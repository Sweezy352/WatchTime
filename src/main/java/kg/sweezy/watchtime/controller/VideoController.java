package kg.sweezy.watchtime.controller;

import kg.sweezy.watchtime.dto.VideoDtoPreview;
import kg.sweezy.watchtime.dto.VideoDtoRequest;
import kg.sweezy.watchtime.dto.VideoDtoResponse;
import kg.sweezy.watchtime.mapper.VideoMapper;
import kg.sweezy.watchtime.service.VideoMinIoService;
import kg.sweezy.watchtime.service.VideoService;
import kg.sweezy.watchtime.utils.ManageTranslation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/video")
public class VideoController {
    private final VideoService videoService;
    private final VideoMinIoService videoMinIoService;
    private final ManageTranslation manageTranslation;

    public VideoController(VideoService videoService, VideoMinIoService videoMinIoService, ManageTranslation manageTranslation) {
        this.videoService = videoService;
        this.videoMinIoService = videoMinIoService;
        this.manageTranslation = manageTranslation;
    }

    @PostMapping("/upload-video")
    public ResponseEntity<VideoDtoResponse> uploadVideo(@ModelAttribute VideoDtoRequest videoDtoRequest,
                                                        @RequestPart MultipartFile videoFile,
                                                        @RequestPart MultipartFile videoPreviewFile) {
        return ResponseEntity.ok(VideoMapper.mapVideoEntityToVideoDtoResponse(videoService.uploadVideo(VideoMapper.mapVideoDtoRequestToVideoEntity(videoDtoRequest), videoFile, videoPreviewFile)));
    }

    @GetMapping("/get-by-id/{videoId}")
    public ResponseEntity<VideoDtoResponse> getVideoById(@PathVariable Long videoId){
        return ResponseEntity.ok(VideoMapper.mapVideoEntityToVideoDtoResponse(videoService.getVideoById(videoId)));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<VideoDtoPreview>> getAllVideo(){
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getAllVideos()));
    }

    @GetMapping("/get-all-by-channel-id")
    public ResponseEntity<List<VideoDtoPreview>> getAllVideoByChannelId(@RequestParam Long channelId){
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getAllVideosByChannel(channelId)));
    }

    @DeleteMapping("/delete-by-id")
    public String deleteVideoById(@RequestParam Long videoId){
        return manageTranslation.getTranslation(videoService.deleteVideo(videoId));
    }

    @GetMapping("/stream-video-by-file-name")
    public ResponseEntity<?> streamVideoByFileName(@RequestParam String fileNameVideo){
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(videoMinIoService.getContentTypeVideo(fileNameVideo)))
                .body(new InputStreamResource(videoMinIoService.getVideoByFileName(fileNameVideo)));
    }

    @GetMapping("/stream-preview-by-file-name")
    public ResponseEntity<?> streamPreviewVideoByFileName(@RequestParam String fileNamePreview){
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(videoMinIoService.getContentTypeVideoPreview(fileNamePreview)))
                .body(new InputStreamResource(videoMinIoService.getVideoPreviewByFileName(fileNamePreview)));
    }
}
