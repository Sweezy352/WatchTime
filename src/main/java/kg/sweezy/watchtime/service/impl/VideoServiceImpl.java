package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.entity.VideoEntity;
import kg.sweezy.watchtime.exception.NotNullVideoException;
import kg.sweezy.watchtime.exception.VideoNotFoundException;
import kg.sweezy.watchtime.exception.VideoUploadException;
import kg.sweezy.watchtime.repository.PreviewVideoRepository;
import kg.sweezy.watchtime.repository.VideoRepository;
import kg.sweezy.watchtime.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final AuthService authService;
    private final VideoMinIoService videoMinIoService;
    private final PreviewVideoRepository previewVideoRepository;
    private final UserService userService;
    private final MediaService mediaService;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository, AuthService authService, VideoMinIoService videoMinIoService, PreviewVideoRepository previewVideoRepository, UserService userService, MediaService mediaService) {
        this.videoRepository = videoRepository;
        this.authService = authService;
        this.videoMinIoService = videoMinIoService;
        this.previewVideoRepository = previewVideoRepository;
        this.userService = userService;
        this.mediaService = mediaService;
    }

    @Override
    public VideoEntity uploadVideo(VideoEntity videoEntity, MultipartFile videoFile, MultipartFile videoPreview) {
        UserEntity userEntity = authService.getCurrentUser();
        if(userEntity != null){
            if(videoEntity == null || videoFile.isEmpty() || videoPreview.isEmpty()) throw new NotNullVideoException("error.videoEntity");
            videoEntity = videoMinIoService.uploadVideo(videoEntity, videoFile);
            videoEntity.setChannel(userEntity);
            VideoEntity video =  videoRepository.save(videoEntity);
            video.setPreviewVideo(previewVideoRepository.save(videoMinIoService.uploadVideoPreview(videoEntity, videoPreview)));
            return video;
        }
        throw new VideoUploadException("error.uploadVideo");
    }

    @Override
    public VideoEntity getVideoById(Long id) {
        return videoRepository.findById(id).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
    }

    @Override
    public List<VideoEntity> getAllVideos() {
        return videoRepository.findAll();
    }

    @Override
    public List<VideoEntity> getAllVideosByChannel(Long channelId) {
        UserEntity userEntity = userService.getUserById(channelId);
        return userEntity.getVideos();
    }

    @Override
    public String deleteVideo(Long id) {
        VideoEntity videoEntity = videoRepository.findById(id).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
        if(videoEntity.getChannel() != authService.getCurrentUser()) throw new VideoNotFoundException("error.videoNotFound");
        videoMinIoService.deleteVideoPreviewByFileName(videoEntity.getPreviewVideo().getFileName());
        videoMinIoService.deleteVideoByFileName(videoEntity.getFileName());
        videoRepository.delete(videoEntity);
        return "success.deleteVideo";
    }
}
