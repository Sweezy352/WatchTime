package kg.sweezy.watchtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "Дто для создания нового пользователя")
public class UserDtoRequest {
    @Schema(description = "Логин пользователя", example = "Sweezy", required = true)
    private String username;
    @Schema(description = "Пароль пользователя", example = "qweqwe", required = true)
    private String password;
    @Schema(description = "Почта пользователя", example = "dautov.292009@gmail.com", required = true)
    private String email;
}
