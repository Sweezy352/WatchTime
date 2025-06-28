package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.IncorrectInputException;
import kg.sweezy.watchtime.exception.UserNotFoundException;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.service.ProfilePictureService;
import kg.sweezy.watchtime.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfilePictureService profilePictureService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ProfilePictureService profilePictureService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profilePictureService = profilePictureService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity register(UserEntity user, MultipartFile profilePicture) {
        if(user.getUsername().isEmpty()
                || user.getPassword().isEmpty()
                || user.getEmail().isEmpty()) throw new IncorrectInputException("");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity userEntity = userRepository.save(user);
        userEntity.setProfilePicture(profilePictureService.uploadProfilePicture(userEntity, profilePicture));
        return userEntity;
    }

    @Override
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("error.userNotFound"));
    }

    @Override
    public List<UserEntity> getAllByUsername(String username) {
        return List.of();
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
