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
@Schema(description = "Дто для возвращения данных пользователя")
public class UserDtoResponse {
    @Schema(description = "Индентификатор пользователя", example = "1")
    private Long id;
    @Schema(description = "Логин пользователя", example = "Sweezy")
    private String username;
    @Schema(description = "Почта пользователя", example = "dautov.292009@gmail.com")
    private String email;
    @Schema(description = "Количество подписчиков у пользователя", example = "50")
    private Long subscribers;
    @Schema(description = "Премиум аккаунт", example = "false")
    private Boolean isPremium;
    @Schema(description = "Дата создания аккаунта", example = "2025-07-11", type = "string", format = "date")
    private LocalDate dateCreated;
    @Schema(description = "Поле с информацией о аватарке пользователя")
    private ImageDto profilePicture;
    @Schema(description = "Список подписок на каналы", implementation = UserDtoPreview.class)
    private List<UserDtoPreview> subscriptionList;
    @Schema(description = "Список видео у пользователя", implementation = VideoDtoPreview.class)
    private List<VideoDtoPreview> videoChannelList;
}
