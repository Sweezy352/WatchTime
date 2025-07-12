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
@Schema(description = "Дто с полным содержимым видео")
public class VideoDtoResponse {
    @Schema(description = "Индинтификатор видео", example = "10")
    private Long id;
    @Schema(description = "Название файла видео", example = "Mortal Kombat 11 2025.01.07")
    private String fileName;
    @Schema(description = "Заголовок видео", example = "Курс по java backend")
    private String title;
    @Schema(description = "Описание видео", example = "В этом курсе мы расмотрим бекенд на языке java")
    private String description;
    @Schema(description = "Количество просмотров видео", example = "2500")
    private Long views;
    @Schema(description = "Количество лайков видео", example = "100")
    private Long likes;
    @Schema(description = "Количество дизлайков видео", example = "50")
    private Long dislikes;
    @Schema(description = "Количество коментариев видео", example = "5")
    private Long amountComments;
    @Schema(description = "Дата создания видео", example = "2025-07-10", type = "string", format = "date")
    private LocalDate dateCreated;
    @Schema(description = "Поле с превью видео")
    private ImageDto previewImage;
    @Schema(description = "Автор видео")
    private UserDtoPreview channel;
}
