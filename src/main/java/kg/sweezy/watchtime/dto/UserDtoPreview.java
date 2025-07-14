package kg.sweezy.watchtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Дто для отображения пользователей во всей системе")
public class UserDtoPreview {
    @Schema(description = "Индинтификатор пользователя", example = "1")
    private Long id;
    @Schema(description = "Логин пользователя", example = "Sweezy")
    private String username;
    @Schema(description = "Дата создания аккаунта", example = "2025-07-11", type = "string", format = "date")
    private LocalDate dateCreated;
    @Schema(description = "Поле с информацией о аватарке пользователя")
    private ImageDto profilePicture;
}
