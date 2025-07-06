package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.entity.VideoEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.exception.NotNullVideoException;
import kg.sweezy.watchtime.exception.VideoNotFoundException;
import kg.sweezy.watchtime.exception.VideoUploadException;
import kg.sweezy.watchtime.repository.PreviewVideoRepository;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.repository.VideoRepository;
import kg.sweezy.watchtime.service.*;
import kg.sweezy.watchtime.utils.ManageTranslation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class VideoServiceImpl extends MediaBaseServiceImpl<VideoEntity> implements VideoService {
    private final VideoRepository videoRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final VideoMinIoService videoMinIoService;
    private final PreviewVideoRepository previewVideoRepository;
    private final UserService userService;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository, AuthService authService, UserRepository userRepository, ManageTranslation manageTranslation, VideoMinIoService videoMinIoService, PreviewVideoRepository previewVideoRepository, UserService userService ) {
        super(authService, manageTranslation);
        this.videoRepository = videoRepository;
        this.authService = authService;
        this.userRepository = userRepository;
        this.videoMinIoService = videoMinIoService;
        this.previewVideoRepository = previewVideoRepository;
        this.userService = userService;
    }

    @Override
    public VideoEntity uploadVideo(VideoEntity videoEntity, MultipartFile videoFile, MultipartFile videoPreview) {
        UserEntity userEntity = authService.getCurrentUser();
        if(userEntity == null) throw new AuthenticationException("error.authentication");

            if(videoEntity == null || videoFile.isEmpty() || videoPreview.isEmpty()) throw new NotNullVideoException("error.videoEntity");
            if(videoEntity.getTitle().replace(" ", "").isEmpty()
                    || videoPreview.getOriginalFilename().replace(" ", "").isEmpty()
                    || videoEntity.getDescription().replace(" ", "").isEmpty()) throw new VideoUploadException("error.videoEntity");

            videoEntity = videoMinIoService.uploadVideo(videoEntity, videoFile);
            videoEntity.setChannel(userEntity);
            VideoEntity video =  videoRepository.save(videoEntity);
            video.setPreviewVideo(previewVideoRepository.save(videoMinIoService.uploadVideoPreview(videoEntity, videoPreview)));
            return video;
    }

    @Override
    public VideoEntity getVideoById(Long id) {
        VideoEntity videoEntity = videoRepository.findById(id).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
        viewByMediaEntity(id);
        return videoEntity;
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
        if(!videoEntity.getChannel().getUsername().equals(authService.getCurrentUser().getUsername())) throw new VideoNotFoundException("error.videoNotFound");
        videoMinIoService.deleteVideoPreviewByFileName(videoEntity.getPreviewVideo().getFileName());
        videoMinIoService.deleteVideoByFileName(videoEntity.getFileName());
        videoRepository.delete(videoEntity);
        return "success.deleteVideo";
    }

    @Override
    protected VideoEntity findMediaById(Long id) {
        return videoRepository.findById(id).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
    }

    @Override
    protected List<VideoEntity> getLiked(UserEntity userEntity) {
        return userEntity.getVideoLiked();
    }

    @Override
    protected List<VideoEntity> getDisliked(UserEntity userEntity) {
        return userEntity.getVideoDisliked();
    }

    @Override
    protected List<VideoEntity> getPlayList(UserEntity userEntity) {
        return userEntity.getVideoPlayList();
    }

    @Override
    protected List<VideoEntity> getHistory(UserEntity userEntity) {
        return userEntity.getVideoHistory();
    }

    @Override
    protected void saveUser(UserEntity user) {
        userRepository.saveAndFlush(user);
    }

    @Override
    protected void saveMedia(VideoEntity media) {
        videoRepository.saveAndFlush(media);
    }
}
