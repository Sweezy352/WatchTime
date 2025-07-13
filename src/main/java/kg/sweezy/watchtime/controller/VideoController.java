package kg.sweezy.watchtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.sweezy.watchtime.dto.*;
import kg.sweezy.watchtime.entity.CommentEntity;
import kg.sweezy.watchtime.exception.BaseException;
import kg.sweezy.watchtime.mapper.CommentsMapper;
import kg.sweezy.watchtime.mapper.VideoMapper;
import kg.sweezy.watchtime.service.VideoMinIoService;
import kg.sweezy.watchtime.service.VideoService;
import kg.sweezy.watchtime.utils.LimitedInputStream;
import kg.sweezy.watchtime.utils.ManageTranslation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Tag(name = "Контроллер для видео")
@RestController
@RequestMapping("/api/video")
@SecurityRequirement(name = "bearerAuth")
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
                                                        @Parameter(description = "Файл превью, которое обязательно")@RequestPart MultipartFile videoPreviewFile) throws BaseException {
        return ResponseEntity.ok(VideoMapper.mapVideoEntityToVideoDtoResponse(videoService.uploadVideo(VideoMapper.mapVideoDtoRequestToVideoEntity(videoDtoRequest), videoFile, videoPreviewFile)));
    }

    @Operation(
            summary = "Метод для получения видео по айди"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео с таким айди не найдено"),
            @ApiResponse(responseCode = "200", description = "Успешное получение видео по айди")
    })
    @GetMapping("/get-by-id/{videoId}")
    public ResponseEntity<VideoDtoResponse> getVideoById(@Parameter(description = "Индинтификатор видео")@PathVariable Long videoId) throws BaseException {
        return ResponseEntity.ok(VideoMapper.mapVideoEntityToVideoDtoResponse(videoService.getVideoById(videoId)));
    }

    @Operation(
            summary = "Метод для получения всех видео",
            description = "Реализовывает Infinite scroll и для этого принимает afterId и limit"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение всех видео")
    @GetMapping("/get-all")
    public ResponseEntity<List<VideoDtoPreview>> getAllVideo(@Parameter(description = "Индинтификатор конечного видео при scroll") @RequestParam(required = false) Long afterId,@Parameter(description = "Лимит на выдачу видео, по дефолту 10") @RequestParam(defaultValue = "10") Integer limit) throws BaseException {
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getAllVideos(afterId, limit)));
    }

    @Operation(
            summary = "Метод для получения всех видео на канале по айди"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Канал с таким айди не найден"),
            @ApiResponse(responseCode = "200", description = "Успешное получение всех видео на канале по айди")
    })
    @GetMapping("/get-all-by-channel-id")
    public ResponseEntity<List<VideoDtoPreview>> getAllVideoByChannelId(@Parameter(description = "Индинтификатор канала") @RequestParam Long channelId) throws BaseException {
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getAllVideosByChannel(channelId)));
    }

    @Operation(
            summary = "Метод для удаления видео по айди"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео с таким айди не найдено"),
            @ApiResponse(responseCode = "403", description = "Пользователю не принадлежит это видео"),
            @ApiResponse(responseCode = "200", description = "Успешное удаление видео")
    })
    @DeleteMapping("/delete-by-id")
    public String deleteVideoById(@Parameter(description = "Индинтификатор видео") @RequestParam Long videoId) throws BaseException {
        return manageTranslation.getTranslation(videoService.deleteVideo(videoId));
    }

    @Operation(
            summary = "Метод для получения видео по названию файла",
            description = "Метод который возвращает само видео по его названию файла, также возвращает видео по частям, чтоб пользователь не загружал полностью все видео"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Файл видео с таким названием не найден"),
            @ApiResponse(responseCode = "200", description = "Успешно получено видео")
    })
    @GetMapping("/stream-video-by-file-name")
    public ResponseEntity<?> streamVideoByFileName(@Parameter(description = "Название файла видео") @RequestParam String fileNameVideo,@Parameter(description = "Header который принимает range для подгрузки части видео") @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException, BaseException {
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

    @Operation(
            summary = "Метод для получения превью видео",
            description = "Метод который возвращает превью видео по его названию файла"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Превью с таким названием файла не найдено"),
            @ApiResponse(responseCode = "200", description = "Успешное получение превью видео")
    })
    @GetMapping("/stream-preview-by-file-name")
    public ResponseEntity<?> streamPreviewVideoByFileName(@Parameter(description = "Название файла превью") @RequestParam String fileNamePreview) throws BaseException {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(videoMinIoService.getContentTypeVideoPreview(fileNamePreview)))
                .body(new InputStreamResource(videoMinIoService.getVideoPreviewByFileName(fileNamePreview)));
    }

    @Operation(
            summary = "Метод для добавления коментария к видео по айди"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео с таким айди не найдено"),
            @ApiResponse(responseCode = "200", description = "Успешно добавлен коментарий к видео")
    })
    //Media for video
    @PostMapping("/add-comment-by-id")
    public ResponseEntity<CommentDtoResponse> addCommentByVideoId(@Parameter(description = "Индинтификатор видео") @RequestParam Long videoId,@Parameter(description = "Тело коментария") @RequestBody CommentDtoRequest commentDtoRequest) throws BaseException {
        return ResponseEntity.ok(CommentsMapper.mapEntityToDtoResponse(videoService.addCommentToMedia(videoId,CommentsMapper.mapDtoRequestToEntity(commentDtoRequest))));
    }

    @Operation(
            summary = "Метод для лайка видео по айди",
            description = "Если видео уже лайкнуто, то метод уберет лайк с видео, и если на видео стоит дизлайк, то метод уберет дизлайк и поставит лайк к видео"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео с таким айди не найден"),
            @ApiResponse(responseCode = "200", description = "Успешно поставлен лайк к видео")
    })
    @PostMapping("/like-video-by-id")
    public ResponseEntity<?> likeVideoById(@Parameter(description = "Индинтификатор видео") @RequestParam Long videoId) throws BaseException {
        videoService.likeMediaById(videoId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Метод для дизлайка видео",
            description = "Если на видео уже стоит дизайлк, то метод уберет дизлайк, если на видео стоит лайк, то метод уберет лайк и поставит дизлайк"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео с таким айди не найден"),
            @ApiResponse(responseCode = "200", description = "Успешно поставлен дизлайк")
    })
    @PostMapping("/dislike-video-by-id")
    public ResponseEntity<?> dislikeVideoById(@Parameter(description = "Индинтификатор видео") @RequestParam Long videoId) throws BaseException {
        videoService.dislikeMediaById(videoId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Метод для добавления видео в плейлист по айди",
            description = "Если видео уже добавлено в плейлист, то метод уберет видео с плейлиста"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео с таким айди не найдено"),
            @ApiResponse(responseCode = "200", description = "Успешно добавлено видео в плейлист")
    })
    @PostMapping("/add-to-play-list-by-id")
    public ResponseEntity<?> addToPlayListById(@Parameter(description = "Индинтификатор видео") @RequestParam Long videoId) throws BaseException {
        videoService.addToPlayList(videoId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Метод для удаления видео с плейлиста по айди"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео с таким айди не найден"),
            @ApiResponse(responseCode = "400", description = "Такого видео нет в плейлисте"),
            @ApiResponse(responseCode = "200", description = "Успешное удаление видео с плейлиста")
    })
    @PostMapping("/remove-from-play-list-by-id")
    public ResponseEntity<?> removeFromPlayListById(@Parameter(description = "Индинтификатор видео") @RequestParam Long videoId) throws BaseException {
        videoService.removeFromPlayList(videoId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Метод для получения всех лайкнутых видео"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение всех лайкнутых видео")
    @GetMapping("/get-liked-videos")
    public ResponseEntity<List<VideoDtoPreview>> getLikedVideos() throws BaseException {
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getLikedVideos()));
    }

    @Operation(
            summary = "Метод для получения всех видео с плейлиста"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение всех видео с плейлиста")
    @GetMapping("/get-play-list")
    public ResponseEntity<List<VideoDtoPreview>> getPlayList() throws BaseException {
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getPlayListVideos()));
    }

    @Operation(
            summary = "Метод для получения всех коментариев под видео"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео с таким айди не найдено"),
            @ApiResponse(responseCode = "200", description = "Успешное получение всех коментариев под видео")
    })
    @GetMapping("/get-all-comments-by-video-id")
    public ResponseEntity<List<CommentDtoResponse>> getAllCommentsByVideoId(@Parameter(description = "Индинтификтор видео") @RequestParam Long videoId) throws BaseException {
        return ResponseEntity.ok(CommentsMapper.mapEntityListToDtoResponseList(videoService.getAllCommentsByVideoId(videoId)));
    }

    @Operation(
            summary = "Метод для получения всех видео по заголовку"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение всех видео по заголовку")
    @GetMapping("/get-videos-by-title")
    public ResponseEntity<List<VideoDtoPreview>> getVideosByTitle(@Parameter(description = "Заголовок видео") @RequestParam String title) throws BaseException {
        return ResponseEntity.ok(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(videoService.getVideoByTitle(title)));
    }

    @Operation(
            summary = "Метод для удаления коментария под видео"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Видео не найдено"),
            @ApiResponse(responseCode = "404", description = "Коментарий под видео не найдено"),
            @ApiResponse(responseCode = "200", description = "Успешное удаление коментария под видео")
    })
    @DeleteMapping("/delete-comment-from-video")
    public ResponseEntity<String> deleteFromVideo(@Parameter(description = "Индинтификатор видео") @RequestParam Long videoId, @Parameter(description = "Индинтификатор коментария") @RequestParam Long commentId) throws BaseException {
        return ResponseEntity.ok(videoService.deleteCommentFromMedia(videoId, commentId));
    }
}
