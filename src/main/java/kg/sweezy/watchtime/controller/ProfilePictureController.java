package kg.sweezy.watchtime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.sweezy.watchtime.entity.ProfilePictureEntity;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.mapper.ImageMapper;
import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.service.ProfilePictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Tag(name = "Контроллер для аватарок пользователей")
@RestController
@RequestMapping("/api/profile-picture")
@SecurityRequirement(name = "bearerAuth")
public class ProfilePictureController {
    private final ProfilePictureService profilePictureService;
    private final AuthService authService;

    @Autowired
    public ProfilePictureController(ProfilePictureService profilePictureService, AuthService authService) {
        this.profilePictureService = profilePictureService;
        this.authService = authService;
    }

    @Operation(
            summary = "Метод для загрузки автара пользователю",
            description = "Метод который загружает аватарку пользователю, принимает в себя картинку"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
                    @ApiResponse(responseCode = "400", description = "Пустые значения в файле, плохой запрос"),
                    @ApiResponse(responseCode = "505", description = "Проблемы с сервером"),
                    @ApiResponse(responseCode = "200", description = "Аватарка успешно загружена")
            }
    )
    @PostMapping("/upload-to-user")
    public ResponseEntity<?> uploadProfilePicture(@Parameter(description = "Картинка - MultipartFile") @RequestPart MultipartFile profilePicture) {
        UserEntity userEntity = authService.getCurrentUser();
        ProfilePictureEntity profilePictureEntity = profilePictureService.uploadProfilePicture(userEntity, profilePicture);
        return ResponseEntity.ok(ImageMapper.mapToImageDto(profilePictureEntity.getId(), profilePictureEntity.getFileName()));
    }

    @Operation(
            summary = "Метод для получения аватарки пользователей",
            description = "Метод который отдает аватарку пользователя для ее отображения"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Аватарка с таким названием файла не найден"),
                    @ApiResponse(responseCode = "505", description = "Проблемы с сервером"),
                    @ApiResponse(responseCode = "200", description = "Успешное получение аватарки пользователя")
            }
    )
    @GetMapping("/get-by-file-name")
    public ResponseEntity<?> getProfilePictureByFileName(@Parameter(description = "Название файла автарки пользователя")@RequestParam String fileName) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(profilePictureService.getContentType(fileName)))
                .body(new InputStreamResource(profilePictureService.getProfilePictureByFileName(fileName)));
    }

    @Operation(
            summary = "Метод для удаления аватарки пользователя"
    )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "404", description = "Аватарка не найдена"),
                    @ApiResponse(responseCode = "505", description = "Проблемы с сервером"),
                    @ApiResponse(responseCode = "200", description = "Аватарка успешно удалена")
            }
    )
    @DeleteMapping("/delete-by-user")
    public ResponseEntity<?> deleteProfilePictureByUser() {
        profilePictureService.deleteProfilePictureByUser(authService.getCurrentUser());
        return ResponseEntity.ok().build();
    }
}
