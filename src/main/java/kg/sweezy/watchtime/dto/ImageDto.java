package kg.sweezy.watchtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Дто с информацией о картинке")
public class ImageDto {
    @Schema(description = "Индинтификатор картинки", example = "5")
    private Long id;
    @Schema(description = "Название файла картинки", example = "Снимок экрана 2025-04-07")
    private String fileName;
}
