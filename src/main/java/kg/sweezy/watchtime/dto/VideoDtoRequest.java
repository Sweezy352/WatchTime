package kg.sweezy.watchtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Дто для создания видео")
public class VideoDtoRequest {
    @Schema(description = "Заголовок видео", example = "Курс по java backend")
    private String title;
    @Schema(description = "Описание видео", example = "В этом курсе мы расмотрим бекенд на языке java")
    private String description;
}
