package kg.sweezy.watchtime.mapper;

import kg.sweezy.watchtime.dto.VideoDtoPreview;
import kg.sweezy.watchtime.dto.VideoDtoRequest;
import kg.sweezy.watchtime.dto.VideoDtoResponse;
import kg.sweezy.watchtime.entity.VideoEntity;

import java.util.List;
import java.util.stream.Collectors;

public class VideoMapper {
    public static VideoEntity mapVideoDtoRequestToVideoEntity(VideoDtoRequest videoDtoRequest) {
        VideoEntity videoEntity = VideoEntity.builder()
                .title(videoDtoRequest.getTitle())
                .description(videoDtoRequest.getDescription())
                .build();
        return videoEntity;
    }

    public static VideoDtoResponse mapVideoEntityToVideoDtoResponse(VideoEntity videoEntity) {
        VideoDtoResponse videoDtoResponse = VideoDtoResponse.builder()
                .id(videoEntity.getId())
                .fileName(videoEntity.getFileName())
                .title(videoEntity.getTitle())
                .description(videoEntity.getDescription())
                .views(videoEntity.getViews())
                .likes(videoEntity.getLikes())
                .dislikes(videoEntity.getDislikes())
                .amountComments(videoEntity.getAmountComments())
                .dateCreated(videoEntity.getDateCreated())
                .build();
        if(videoEntity.getPreviewVideo() != null) videoDtoResponse.setPreviewImage(ImageMapper.mapToImageDto(videoEntity.getPreviewVideo().getId(), videoEntity.getPreviewVideo().getFileName()));
        if(videoEntity.getChannel() != null) videoDtoResponse.setChannel(UserMapper.mapEntityToDtoPreview(videoEntity.getChannel()));
        if(videoEntity.getComments() != null) videoDtoResponse.setComments(CommentsMapper.mapEntityListToDtoResponseList(videoEntity.getComments()));
        return videoDtoResponse;
    }

    public static VideoDtoPreview mapVideoEntityToVideoDtoPreview(VideoEntity videoEntity) {
        VideoDtoPreview videoDtoPreview = VideoDtoPreview.builder()
                .id(videoEntity.getId())
                .fileName(videoEntity.getFileName())
                .title(videoEntity.getTitle())
                .views(videoEntity.getViews())
                .dateCreated(videoEntity.getDateCreated())
                .channel(UserMapper.mapEntityToDtoPreview(videoEntity.getChannel()))
                .build();
        if(videoEntity.getPreviewVideo() != null) videoDtoPreview.setVideoPreview(ImageMapper.mapToImageDto(videoEntity.getPreviewVideo().getId(), videoEntity.getPreviewVideo().getFileName()));
        return videoDtoPreview;
    }

    public static List<VideoDtoPreview> mapVideoEntityListToVideoDtoPreviewList(List<VideoEntity> videoEntityList) {
        return videoEntityList.stream().map(VideoMapper::mapVideoEntityToVideoDtoPreview).collect(Collectors.toList());
    }
}
