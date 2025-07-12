package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.dto.UserDtoPreview;
import kg.sweezy.watchtime.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    public UserEntity register(UserEntity user, MultipartFile profilePicture);
    public UserEntity getUserById(Long id);
    public List<UserEntity> getAllByUsername(String username);
    public List<UserEntity> getAllUsers();
    public String subscribeByChannelId(Long channelId);
    public List<UserEntity> getSubscriptionsChannel();
}
