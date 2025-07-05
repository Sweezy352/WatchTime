package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.RoleEntity;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.IncorrectInputException;
import kg.sweezy.watchtime.exception.UserNotFoundException;
import kg.sweezy.watchtime.repository.RoleRepository;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.service.ProfilePictureService;
import kg.sweezy.watchtime.service.UserService;
import kg.sweezy.watchtime.utils.ReplaceEmptySpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfilePictureService profilePictureService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReplaceEmptySpace replaceEmptySpace;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ProfilePictureService profilePictureService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ReplaceEmptySpace replaceEmptySpace) {
        this.userRepository = userRepository;
        this.profilePictureService = profilePictureService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.replaceEmptySpace = replaceEmptySpace;
    }

    @Override
    public UserEntity register(UserEntity user, MultipartFile profilePicture) {
        if(user.getUsername().replace(" ", "").isEmpty()
                || user.getPassword().replace(" ", "").isEmpty()
                || user.getEmail().replace(" ", "").isEmpty()) throw new IncorrectInputException("error.incorrectInput");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        RoleEntity role = roleRepository.findById(1L).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setUsers(List.of(user));
        user.setRoles(List.of(role));
        UserEntity userEntity = userRepository.save(user);

        if(profilePicture != null && !profilePicture.isEmpty()) userEntity.setProfilePicture(profilePictureService.uploadProfilePicture(userEntity, profilePicture));
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
