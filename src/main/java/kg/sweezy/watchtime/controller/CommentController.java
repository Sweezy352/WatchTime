package kg.sweezy.watchtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.sweezy.watchtime.dto.CommentDtoRequest;
import kg.sweezy.watchtime.dto.CommentDtoResponse;
import kg.sweezy.watchtime.exception.BaseException;
import kg.sweezy.watchtime.mapper.CommentsMapper;
import kg.sweezy.watchtime.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Контроллер для коментариев")
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(
            summary = "Метод изменения коментария",
            description = "Метод который изменяет коментарий по его айди"
    )
    @ApiResponses(
            {@ApiResponse(responseCode = "404", description = "Коментарий не найден"),
                    @ApiResponse(responseCode = "400", description = "У коментария пустые значения"),
                    @ApiResponse(responseCode = "200", description = "Коментарий успешно обновлен")
            }
    )
    @PostMapping("/update-comment-by-id")
    public ResponseEntity<CommentDtoResponse> updateCommentById(@Parameter(description = "Индентификатор коментария") @RequestParam Long commentId, @Parameter(description = "Тело обновленного коментария") @RequestBody CommentDtoRequest commentDtoRequest) throws BaseException {
        return ResponseEntity.ok(CommentsMapper.mapEntityToDtoResponse(commentService.updateComment(commentId, CommentsMapper.mapDtoRequestToEntity(commentDtoRequest))));
    }

    @Operation(
            summary = "Метод для удаления коментария",
            description = "Метод который удаляет коментарий по его айди"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Коментарий не найден"),
                    @ApiResponse(responseCode = "200", description = "Коментарий успешно удален")
            }
    )
    @DeleteMapping("/delete-by-id")
    public String deleteCommentById(@Parameter(description = "Индентификатор коментария") @RequestParam Long commentId) throws BaseException {
        return commentService.deleteCommentById(commentId);
    }

    @Operation(
            summary = "Метод который лайкает коментарий",
            description = "Метод для лайка коментария по его айди," +
                    " если коментарий уже лайкнут, то лайк убирается с коментария," +
                    " если на коментарии стоит дизлайк, то метод убирает дизлайк и ставит лайк"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Коментарий не найден"),
                    @ApiResponse(responseCode = "200", description = "Поставлен лайк или убран лайк")
            }
    )
    @PostMapping("/like-by-id")
    public ResponseEntity<?> likeCommentById(@Parameter(description = "Индинтификатор коментария") @RequestParam Long commentId) throws BaseException {
        commentService.likeMediaById(commentId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Метод для дизлайка коментария",
            description = "Метод который ставит дизлайк на коментарий по его айди," +
                    " и если на коментарии уже стоит дизлайк то метод уберет дизлайк," +
                    " а если на коментарии стоит лайк, то метод уберет лайк и поставит дизлайк"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Коментарий не найден"),
                    @ApiResponse(responseCode = "200", description = "Поставлен дизлайк или убран дизлайк")
            }
    )
    @PostMapping("/dislike-by-id")
    public ResponseEntity<?> dislikeCommentById(@Parameter(description = "Индинтификатор коментария") @RequestParam Long commentId) throws BaseException {
        commentService.dislikeMediaById(commentId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Метод для добавления коментария",
            description = "Метод который добавляет коментарий к уже существующему по его айди"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Коментарий не найден"),
                    @ApiResponse(responseCode = "400", description = "Коментарий с пустыми значениями"),
                    @ApiResponse(responseCode = "200", description = "Успешно добавлен коментарий")
            }
    )
    @PostMapping("/add-comment-by-id")
    public ResponseEntity<CommentDtoResponse> addCommentById(@Parameter(description = "Индинтификатор коментария") @RequestParam Long commentId, @Parameter(description = "Тело добавляющегося коментария") @RequestBody CommentDtoRequest commentDtoRequest) throws BaseException {
        return ResponseEntity.ok(CommentsMapper.mapEntityToDtoResponse(commentService.addCommentToMedia(commentId, CommentsMapper.mapDtoRequestToEntity(commentDtoRequest))));
    }
}
