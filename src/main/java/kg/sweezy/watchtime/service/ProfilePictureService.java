package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.dto.ImageDto;
import kg.sweezy.watchtime.entity.ProfilePictureEntity;
import kg.sweezy.watchtime.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ProfilePictureService {
    public ProfilePictureEntity uploadProfilePicture(UserEntity userEntity, MultipartFile file);
    public InputStream getProfilePictureByFileName(String fileName);
    public String getContentType(String fileName);
    public void deleteProfilePictureByUser(UserEntity userEntity);
}
