package kg.sweezy.watchtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Дто для не полного показа видео")
public class VideoDtoPreview {
    @Schema(description = "Индинтификатор видео", example = "55")
    private Long id;
    @Schema(description = "Навазние файла видео", example = "Mortal Kombat 11 2025.01.07")
    private String fileName;
    @Schema(description = "Заголовок видео", example = "Курс для java backend")
    private String title;
    @Schema(description = "Количество просмотров видео", example = "2500")
    private Long views;
    @Schema(description = "Поле превью видео")
    private ImageDto videoPreview;
    @Schema(description = "Дата создания видео", example = "2025-07-10", type = "string", format = "date")
    private LocalDate dateCreated;
    @Schema(description = "Автор видео")
    private UserDtoPreview channel;
}
