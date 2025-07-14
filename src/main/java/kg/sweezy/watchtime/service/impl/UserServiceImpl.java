package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.dto.UserDtoPreview;
import kg.sweezy.watchtime.entity.RoleEntity;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.exception.IncorrectInputException;
import kg.sweezy.watchtime.exception.UserNotFoundException;
import kg.sweezy.watchtime.repository.RoleRepository;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.service.AuthService;
import kg.sweezy.watchtime.service.ProfilePictureService;
import kg.sweezy.watchtime.service.UserService;
import kg.sweezy.watchtime.utils.ManageTranslation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ManageTranslation manageTranslation;
    private final AuthService authService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ProfilePictureService profilePictureService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ManageTranslation manageTranslation, AuthService authService) {
        this.userRepository = userRepository;
        this.profilePictureService = profilePictureService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.manageTranslation = manageTranslation;
        this.authService = authService;
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
        return userRepository.findByUsernameStartingWith(username);
    }

    @Override
    public List<UserEntity> getAllUsers(Long afterId, Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        if(afterId == null) return userRepository.findAll(pageable).getContent();
        return userRepository.findByIdGreaterThanOrderByIdAsc(afterId, pageable);
    }

    @Override
    public String subscribeByChannelId(Long channelId) {
        UserEntity subscriber = authService.getCurrentUser();
        UserEntity channel = userRepository.findById(channelId).orElseThrow(() -> new UserNotFoundException("error.userNotFound"));
        if(subscriber == null) throw new AuthenticationException("error.authentication");

        if(subscriber != null && channel != null){
            if(!subscriber.getSubscriptionList().contains(channel)) {
                subscriber.getSubscriptionList().add(channel);
                channel.getSubscribersList().add(subscriber);
                channel.setSubscribers(channel.getSubscribers() + 1);
                userRepository.saveAndFlush(channel);
                userRepository.saveAndFlush(subscriber);
                return manageTranslation.getTranslation("success.subscription");
            }else if(subscriber.getSubscriptionList().contains(channel)){
                subscriber.getSubscriptionList().remove(channel);
                channel.getSubscribersList().remove(subscriber);
                channel.setSubscribers(channel.getSubscribers() - 1);
                userRepository.saveAndFlush(channel);
                userRepository.saveAndFlush(subscriber);
                return manageTranslation.getTranslation("success.unSubscribe");
            }
        }
        return "";
    }

    @Override
    public List<UserEntity> getSubscriptionsChannel() {
        UserEntity userEntity = authService.getCurrentUser();
        if(userEntity == null) throw new AuthenticationException("error.userNotFound");
        return userEntity.getSubscriptionList();
    }
}
