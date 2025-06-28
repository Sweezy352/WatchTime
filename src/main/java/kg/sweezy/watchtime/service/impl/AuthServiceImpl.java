package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.security.JwtHandler;
import kg.sweezy.watchtime.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHandler jwtHandler;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtHandler jwtHandler) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtHandler = jwtHandler;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new AuthenticationException("error.authentication"));
    }

    @Override
    public String login(String username, String password) {
        log.info("----->>>> Пришел логин: " + username);
        log.info("----->>>> Пришел пароль: " + password);
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new AuthenticationException("error.authentication"));
        log.info("----->>>> Пароль пользователя: " + user.getPassword());
        if(!passwordEncoder.matches(password, user.getPassword()));
        return jwtHandler.jwtGenerateToken(user);
    }

    @Override
    public UserEntity getCurrentUser() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
