package kg.sweezy.watchtime.controller;

import kg.sweezy.watchtime.dto.UserDtoPreview;
import kg.sweezy.watchtime.dto.UserDtoRequest;
import kg.sweezy.watchtime.dto.UserDtoResponse;
import kg.sweezy.watchtime.mapper.UserMapper;
import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDtoResponse> register(@RequestBody UserDtoRequest userDtoRequest, @RequestParam(required = false) MultipartFile profilePicture) {
        return ResponseEntity.ok(UserMapper.mapEntityToDtoResponse(userService.register(UserMapper.mapDtoToEntity(userDtoRequest), profilePicture)));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<UserDtoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(UserMapper.mapEntityToDtoResponse(userService.getUserById(id)));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<UserDtoPreview>> getAll(){
        return ResponseEntity.ok(UserMapper.mapEntityToDtoPreviewList(userService.getAllUsers()));
    }
}
