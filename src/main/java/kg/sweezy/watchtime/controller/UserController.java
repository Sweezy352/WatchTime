package kg.sweezy.watchtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.sweezy.watchtime.dto.UserDtoPreview;
import kg.sweezy.watchtime.dto.UserDtoRequest;
import kg.sweezy.watchtime.dto.UserDtoResponse;
import kg.sweezy.watchtime.mapper.UserMapper;
import kg.sweezy.watchtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Контроллер для пользователей")
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Метод регистрации пользователей в системе",
            description = "Метод для регистрации пользователей, по желанию можно сразу загрузить аватарку"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "400", description = "Пустые значения"),
                    @ApiResponse(responseCode = "505", description = "Проблемы с сервером"),
                    @ApiResponse(responseCode = "200", description = "Успешная регистрация пользователя")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserDtoResponse> register(@Parameter(description = "Тело пользователя в качестве ModelAttribute") @ModelAttribute("user") UserDtoRequest userDtoRequest,@Parameter(description = "Файл аватарки, не обязательна к загрузке") @RequestPart(required = false) MultipartFile profilePicture) {
        return ResponseEntity.ok(UserMapper.mapEntityToDtoResponse(userService.register(UserMapper.mapDtoToEntity(userDtoRequest), profilePicture)));
    }

    @Operation(
            summary = "Метод для получения пользователя по айди"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Пользователь с таким айди не найден"),
                    @ApiResponse(responseCode = "200", description = "Успешное получение пользователя по айди")
            }
    )
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<UserDtoResponse> getById(@Parameter(description = "Индинтификатор пользователя") @PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.mapEntityToDtoResponse(userService.getUserById(id)));
    }

    @Operation(
            summary = "Метод для получения списка пользователей",
            description = "Реализовывает Infinite scroll и для этого принимает afterId и limit"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей")
    @GetMapping("/get-all")
    public ResponseEntity<List<UserDtoPreview>> getAll(@Parameter(description = "Индинтификатор конечного пользователя при скролле") @RequestParam(required = false) Long afterId, @Parameter(description = "Лимит на выдачу пользователей, по дефолту 10")@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(UserMapper.mapEntityToDtoPreviewList(userService.getAllUsers(afterId, limit)));
    }

    @Operation(
            summary = "Метод оформления подписки на другого пользователя",
            description = "Метод который оформляет подписку на другого пользователя, если подписка уже стоит, то метод убирает подписку на канал"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Пользователь с таким айди не найден"),
                    @ApiResponse(responseCode = "200", description = "Успешная подписка на канал или успешная отписка от канала")
            }
    )
    @PostMapping("/subscribe")
    public String subscribeByChannelId(@Parameter(description = "Индинтификатор канала") @RequestParam Long channelId){
        return userService.subscribeByChannelId(channelId);
    }

    @Operation(
            summary = "Метод получения всех подписанных каналов"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение всех подписанных каналов")
    @GetMapping("/get-subscription-channels")
    public ResponseEntity<List<UserDtoPreview>> getSubscriptionChannels(){
        return ResponseEntity.ok(UserMapper.mapEntityToDtoPreviewList(userService.getSubscriptionsChannel()));
    }

    @Operation(
            summary = "Метод для поиска каналов по их названию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Канал с таким названием не найден"),
            @ApiResponse(responseCode = "200", description = "Успешно получен список каналов по названию")
    })
    @GetMapping("/get-channel-by-name/{name}")
    public ResponseEntity<List<UserDtoPreview>> getChannelsByName(@Parameter(description = "Название канала")@PathVariable String name){
        return ResponseEntity.ok(UserMapper.mapEntityToDtoPreviewList(userService.getAllByUsername(name)));
    }
}
