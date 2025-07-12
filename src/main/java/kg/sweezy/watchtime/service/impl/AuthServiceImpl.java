package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.entity.UuIdCodeEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.exception.IncorrectCodeException;
import kg.sweezy.watchtime.exception.IncorrectInputException;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.repository.UuidCodeRepository;
import kg.sweezy.watchtime.security.JwtHandler;
import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.service.MailService;
import kg.sweezy.watchtime.utils.ManageTranslation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHandler jwtHandler;
    private final MailService mailService;
    private final ManageTranslation manageTranslation;
    private final UuidCodeRepository uuidCodeRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtHandler jwtHandler, MailService mailService, ManageTranslation manageTranslation, UuidCodeRepository uuidCodeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtHandler = jwtHandler;
        this.mailService = mailService;
        this.manageTranslation = manageTranslation;
        this.uuidCodeRepository = uuidCodeRepository;
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
        if(!passwordEncoder.matches(password, user.getPassword())) throw new AuthenticationException("error.authentication");
        return jwtHandler.jwtGenerateToken(user);
    }

    @Override
    public UserEntity getCurrentUser() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public String recoveryPassword(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new AuthenticationException("error.userNotFoundWithSuchEmail"));
        String subject = manageTranslation.getTranslation("notification.subjectForRecovery");
        String uuid = UUID.randomUUID().toString();
        mailService.verificationEmail(email, subject, uuid);
        UuIdCodeEntity uuIdCodeEntity = UuIdCodeEntity.builder().email(email).code(uuid).build();
        uuidCodeRepository.save(uuIdCodeEntity);
        return manageTranslation.getTranslation("notification.recoveryPassword");
    }

    @Override
    public UserEntity confirmRecoveryPassword(String code,String password, String confirmPassword) {
        UuIdCodeEntity codeEntity = uuidCodeRepository.findByCode(code).orElseThrow(() -> new IncorrectCodeException("error.incorrectCode"));
        UserEntity userEntity = userRepository.findByEmail(codeEntity.getEmail()).orElseThrow(() -> new AuthenticationException("error.authentication"));
        if(!password.equals(confirmPassword)) throw new IncorrectInputException("error.passwordCompare");
        userEntity.setPassword(passwordEncoder.encode(password));
        return userRepository.saveAndFlush(userEntity);
    }
}
