package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.dto.ImageDto;
import kg.sweezy.watchtime.entity.ProfilePictureEntity;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.exception.FileIsNotExistException;
import kg.sweezy.watchtime.exception.UserNotFoundException;
import kg.sweezy.watchtime.repository.ProfilePictureRepository;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.service.MinIoService;
import kg.sweezy.watchtime.service.ProfilePictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class ProfilePictureServiceImpl implements ProfilePictureService {
    private final MinIoService minIoService;
    private final ProfilePictureRepository profilePictureRepository;
    @Value("${minio.bucket.name.profilePicture}")
    private String bucketName;

    @Autowired
    public ProfilePictureServiceImpl(MinIoService minIoService, ProfilePictureRepository profilePictureRepository) {
        this.minIoService = minIoService;
        this.profilePictureRepository = profilePictureRepository;
    }

    @Override
    public ProfilePictureEntity uploadProfilePicture(UserEntity userEntity, MultipartFile file) {
        minIoService.insureBucketExist(bucketName);
        if(file != null && !file.isEmpty()) {
            if(userEntity.getProfilePicture() != null) {
                minIoService.deleteFile(userEntity.getProfilePicture().getFileName(), bucketName);
                profilePictureRepository.deleteById(userEntity.getProfilePicture().getId());
            }
            minIoService.upload(file, bucketName);
            ProfilePictureEntity profilePictureEntity = new ProfilePictureEntity();
            profilePictureEntity.setFileName(file.getOriginalFilename());
            profilePictureEntity.setUser(userEntity);
            userEntity.setProfilePicture(profilePictureEntity);
            return profilePictureRepository.save(profilePictureEntity);
        }
        return null;
    }

    @Override
    public InputStream getProfilePictureByFileName(String fileName) {
        return minIoService.streamFile(bucketName, fileName);
    }

    @Override
    public String getContentType(String fileName) {
        return minIoService.getContent(bucketName, fileName);
    }

    @Override
    public void deleteProfilePictureByUser(UserEntity userEntity) {
        try{
            if(userEntity.getProfilePicture() != null) {
                minIoService.deleteFile(userEntity.getProfilePicture().getFileName(), bucketName);
                profilePictureRepository.deleteById(userEntity.getProfilePicture().getId());
                return;
            }else{
                throw new FileIsNotExistException("error.fileDoesNotExist");
            }
        }catch (FileIsNotExistException e) {
            throw new FileIsNotExistException(e.getMessage());
        }
    }
}
