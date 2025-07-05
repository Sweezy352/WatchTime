package kg.sweezy.watchtime.controller;

import kg.sweezy.watchtime.dto.UserDtoResponse;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.mapper.UserMapper;
import kg.sweezy.watchtime.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) throws AuthenticationException {
        return authService.login(username, password);
    }

    @GetMapping("/get-current")
    public UserDtoResponse getCurrent() throws AuthenticationException {
        return UserMapper.mapEntityToDtoResponse(authService.getCurrentUser());
    }
}
