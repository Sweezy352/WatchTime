package kg.sweezy.watchtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Дто с полным содержимым коментария")
public class CommentDtoResponse {
    @Schema(description = "Индинтификатор коментария", example = "6")
    private Long id;
    @Schema(description = "Контент коментария", example = "Плохое видео, мне не понравилось")
    private String content;
    @Schema(description = "Количество лайков коментария", example = "10")
    private Long likes;
    @Schema(description = "Количество дизлайков коментария", example = "15")
    private Long dislikes;
    @Schema(description = "Дата создания коментария", example = "2025-07-15", type = "string", format = "date")
    private LocalDate dateCreated;
    @Schema(description = "Автор коментария")
    private UserDtoPreview userDtoPreview;
    @Schema(description = "Список ответов над коментарием")
    private List<CommentDtoResponse> comments;
}
