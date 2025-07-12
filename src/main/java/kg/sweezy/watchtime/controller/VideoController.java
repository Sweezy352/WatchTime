package kg.sweezy.watchtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.sweezy.watchtime.dto.*;
import kg.sweezy.watchtime.entity.CommentEntity;
import kg.sweezy.watchtime.mapper.CommentsMapper;
import kg.sweezy.watchtime.mapper.VideoMapper;
import kg.sweezy.watchtime.service.VideoMinIoService;
import kg.sweezy.watchtime.service.VideoService;
import kg.sweezy.watchtime.utils.LimitedInputStream;
import kg.sweezy.watchtime.utils.ManageTranslation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Tag(name = "Контроллер для видео")
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

    @Operation(
            summary = "Метод для публикации видео",
            description = "Метод который загружает видео на канал пользователя, принимает тело видео, файл видео и файл превью для видео"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Содержание файла видео пустое"),
            @ApiResponse(responseCode = "400", description = "Содержание видео пустое"),
            @ApiResponse(responseCode = "400", description = "Содержание файла превью пустое"),
            @ApiResponse(responseCode = "505", description = "Проблемы с сервером"),
            @ApiResponse(responseCode = "200", description = "Успешная публикация видео на канал")
    })
    @PostMapping("/upload-video")
    public ResponseEntity<VideoDtoResponse> uploadVideo(@Parameter(description = "Тело видео в виде ModelAttribute") @ModelAttribute VideoDtoRequest videoDtoRequest,
                                                        @Parameter(description = "Файл видео, которое обязательно")@RequestPart MultipartFile videoFile,
                                                        @Parameter(description = "Файл превью, которое обязательно")@RequestPart MultipartFile videoPreviewFile) {
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
    public ResponseEntity<?> streamVideoByFileName(@RequestParam String fileNameVideo, @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
        InputStream video = videoMinIoService.getVideoByFileName(fileNameVideo);
        String contentType = videoMinIoService.getContentTypeVideo(fileNameVideo);
        Long fileSize = videoMinIoService.getFileSizeByFileName(fileNameVideo);


        Long start = 0L;
        Long end = fileSize - 1;

        if(rangeHeader != null && rangeHeader.startsWith("bytes=")){
            String[] rangeHeaders = rangeHeader.substring(6).split("-");
            start = Long.parseLong(rangeHeaders[0]);
            if(rangeHeaders.length > 1 && !rangeHeaders[1].isEmpty()){
                end = Long.parseLong(rangeHeaders[1]);
            }
        }

        Long contentLength = end - start + 1;


        video.skip(start);

        InputStreamResource inputStreamResource = new InputStreamResource(new LimitedInputStream(video, contentLength));

        return ResponseEntity.status(rangeHeader != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                .header("Content-Type", contentType)
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", String.valueOf(contentLength))
                .header("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize))
                .body(inputStreamResource);
    }

    @GetMapping("/stream-preview-by-file-name")
    public ResponseEntity<?> streamPreviewVideoByFileName(@RequestParam String fileNamePreview){
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(videoMinIoService.getContentTypeVideoPreview(fileNamePreview)))
                .body(new InputStreamResource(videoMinIoService.getVideoPreviewByFileName(fileNamePreview)));
    }


    //Media for video
    @PostMapping("/add-comment-by-id")
    public ResponseEntity<CommentDtoResponse> addCommentByVideoId(@RequestParam Long videoId, @RequestBody CommentDtoRequest commentDtoRequest){
        return ResponseEntity.ok(CommentsMapper.mapEntityToDtoResponse(videoService.addCommentToMedia(videoId,CommentsMapper.mapDtoRequestToEntity(commentDtoRequest))));
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

    @GetMapping("/get-liked-videos")
    public ResponseEntity<List<VideoDtoPreview>> getLikedVideos(){
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getLikedVideos()));
    }

    @GetMapping("/get-play-list")
    public ResponseEntity<List<VideoDtoPreview>> getPlayList(){
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getPlayListVideos()));
    }

    @GetMapping("/get-all-comments-by-video-id")
    public ResponseEntity<List<CommentDtoResponse>> getAllCommentsByVideoId(@RequestParam Long videoId){
        return ResponseEntity.ok(CommentsMapper.mapEntityListToDtoResponseList(videoService.getAllCommentsByVideoId(videoId)));
    }
}
