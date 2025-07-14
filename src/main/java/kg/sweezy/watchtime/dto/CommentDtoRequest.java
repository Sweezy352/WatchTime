package kg.sweezy.watchtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Дто для добавления коментария")
public class CommentDtoRequest {
    @Schema(description = "Контент коментария", example = "Хорошее видео, очень понравилось")
    private String content;
}
