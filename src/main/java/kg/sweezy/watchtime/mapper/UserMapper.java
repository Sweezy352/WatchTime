package kg.sweezy.watchtime.mapper;

import kg.sweezy.watchtime.dto.ImageDto;
import kg.sweezy.watchtime.dto.UserDtoPreview;
import kg.sweezy.watchtime.dto.UserDtoRequest;
import kg.sweezy.watchtime.dto.UserDtoResponse;
import kg.sweezy.watchtime.entity.ProfilePictureEntity;
import kg.sweezy.watchtime.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserEntity mapDtoToEntity(UserDtoRequest userDtoRequest){
        return new UserEntity().builder()
                .username(userDtoRequest.getUsername().replace(" ", ""))
                .password(userDtoRequest.getPassword())
                .email(userDtoRequest.getEmail().replace(" ", ""))
                .build();
    }

    public static UserDtoResponse mapEntityToDtoResponse(UserEntity userEntity){
        UserDtoResponse userDtoResponse = UserDtoResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .subscribers(userEntity.getSubscribers())
                .isPremium(userEntity.getIsPremium())
                .dateCreated(userEntity.getDateCreated())
                .build();
                if(userEntity.getSubscriptionList() != null)userDtoResponse.setSubscriptionList(mapEntityToDtoPreviewList(userEntity.getSubscriptionList()));
                if(userEntity.getVideos() != null)userDtoResponse.setVideoChannelList(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(userEntity.getVideos()));
                if(userEntity.getVideoLiked() != null)userDtoResponse.setVideoLikedList(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(userEntity.getVideoLiked()));
                if(userEntity.getVideoPlayList() != null)userDtoResponse.setVideoPlayList(VideoMapper.mapVideoEntityListToVideoDtoPreviewList(userEntity.getVideoPlayList()));

        if(userEntity.getProfilePicture() != null){
            ProfilePictureEntity profilePictureEntity = userEntity.getProfilePicture();
            userDtoResponse.setProfilePicture(ImageMapper.mapToImageDto(profilePictureEntity.getId(), profilePictureEntity.getFileName()));
        }
        return userDtoResponse;
    }

    public static UserDtoPreview mapEntityToDtoPreview(UserEntity userEntity){
        UserDtoPreview userDtoPreview = UserDtoPreview.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .dateCreated(userEntity.getDateCreated())
                .build();
        if(userEntity.getProfilePicture() != null){
            ProfilePictureEntity profilePictureEntity = userEntity.getProfilePicture();
            userDtoPreview.setProfilePicture(ImageMapper.mapToImageDto(profilePictureEntity.getId(), profilePictureEntity.getFileName()));
        }
        return userDtoPreview;
    }

    public static List<UserDtoPreview> mapEntityToDtoPreviewList(List<UserEntity> userEntityList){
        return userEntityList.stream().map(UserMapper::mapEntityToDtoPreview).collect(Collectors.toList());
    }
}
