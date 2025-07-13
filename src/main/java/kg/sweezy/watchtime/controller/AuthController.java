package kg.sweezy.watchtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.sweezy.watchtime.dto.UserDtoResponse;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.exception.BaseException;
import kg.sweezy.watchtime.mapper.UserMapper;
import kg.sweezy.watchtime.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Вход в аккаунт",
            description = "Принимает имя и пароль пользователя для авторизации пользователя"
    )
    @ApiResponses(
            {@ApiResponse(responseCode = "400",description = "Неверный логин или пароль"),
            @ApiResponse(responseCode = "200", description = "Успешно выполнено")}
    )
    @PostMapping("/login")
    public String login(@Parameter(description = "Логин пользователя") @RequestParam String username,
                        @Parameter(description = "Пароль пользователя") @RequestParam String password
    ) throws BaseException {
        return authService.login(username, password);
    }

    @Operation(
            summary = "Метод получения текущего пользователя",
            description = "Возвращает информацию о текущем пользователе "
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/get-current")
    public ResponseEntity<UserDtoResponse> getCurrent() throws BaseException {
        return ResponseEntity.ok(UserMapper.mapEntityToDtoResponse(authService.getCurrentUser()));
    }

    @Operation(
            summary = "Метод для восстановления пароля",
            description = "Генерирует специальный код и отправляет на почту пользователя"
    )
    @ApiResponses(
            {@ApiResponse(responseCode = "404", description = "Пользователь с такой почтой не найден в системе"),
            @ApiResponse(responseCode = "200", description = "Успешная отправка кода на почту")}
    )
    @PostMapping("/recovery-password")
    public ResponseEntity<String> recoveryPassword(@Parameter(description = "Почта пользователя") @RequestParam String email) throws BaseException {
        return ResponseEntity.ok(authService.recoveryPassword(email));
    }

    @Operation(
            summary = "Метод для подтверждения кода",
            description = "Подтверждает код, и пользователь может создать новый пароль"
    )
    @PostMapping("/confirm-code/{code}")
    public ResponseEntity<UserDtoResponse> confirmCode(@PathVariable String code, @RequestParam String password, @RequestParam String confirmPassword) throws BaseException {
        return ResponseEntity.ok(UserMapper.mapEntityToDtoResponse(authService.confirmRecoveryPassword(code, password, confirmPassword)));
    }
}
