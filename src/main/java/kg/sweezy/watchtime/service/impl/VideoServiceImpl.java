package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.CommentEntity;
import kg.sweezy.watchtime.entity.UserEntity;
import kg.sweezy.watchtime.entity.VideoEntity;
import kg.sweezy.watchtime.exception.AuthenticationException;
import kg.sweezy.watchtime.exception.NotNullVideoException;
import kg.sweezy.watchtime.exception.VideoNotFoundException;
import kg.sweezy.watchtime.exception.VideoUploadException;
import kg.sweezy.watchtime.repository.CommentRepository;
import kg.sweezy.watchtime.repository.PreviewVideoRepository;
import kg.sweezy.watchtime.repository.UserRepository;
import kg.sweezy.watchtime.repository.VideoRepository;
import kg.sweezy.watchtime.service.*;
import kg.sweezy.watchtime.utils.ManageTranslation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CommentRepository commentRepository;
    private final MailService mailService;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository, AuthService authService, UserRepository userRepository, ManageTranslation manageTranslation, VideoMinIoService videoMinIoService, PreviewVideoRepository previewVideoRepository, UserService userService, CommentRepository commentRepository, MailService mailService) {
        super(authService, manageTranslation, commentRepository);
        this.videoRepository = videoRepository;
        this.authService = authService;
        this.userRepository = userRepository;
        this.videoMinIoService = videoMinIoService;
        this.previewVideoRepository = previewVideoRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.mailService = mailService;
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

        mailService.notifyAllSubscribers(userEntity, video, manageTranslation.getTranslation("success.notifyAboutVideo") + userEntity.getUsername());
            return video;
    }

    @Override
    public VideoEntity getVideoById(Long id) {
        VideoEntity videoEntity = videoRepository.findById(id).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
        viewByMediaEntity(id);
        addToHistoryVideo(videoEntity);
        return videoEntity;
    }

    @Override
    public List<VideoEntity> getAllVideos(Long afterId, Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        if(afterId == null) return videoRepository.findAll(pageable).getContent();

        return videoRepository.findByIdGreaterThanOrderByIdAsc(afterId, pageable);
    }

    @Override
    public List<VideoEntity> getAllVideosByChannel(Long channelId) {
        UserEntity userEntity = userService.getUserById(channelId);
        return userEntity.getVideos();
    }

    @Override
    @CacheEvict(value = "videos", key = "#id", allEntries = true)
    public String deleteVideo(Long id) {
        VideoEntity videoEntity = videoRepository.findById(id).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
        if(!videoEntity.getChannel().getUsername().equals(authService.getCurrentUser().getUsername())) throw new VideoNotFoundException("error.videoNotFound");
        videoMinIoService.deleteVideoPreviewByFileName(videoEntity.getPreviewVideo().getFileName());
        videoMinIoService.deleteVideoByFileName(videoEntity.getFileName());
        videoRepository.delete(videoEntity);
        return "success.deleteVideo";
    }

    @Override
    public List<VideoEntity> getLikedVideos() {
        UserEntity userEntity = authService.getCurrentUser();
        if(userEntity == null) throw new AuthenticationException("error.authentication");
        return userEntity.getVideoLiked();
    }

    @Override
    public List<VideoEntity> getPlayListVideos() {
        UserEntity userEntity = authService.getCurrentUser();
        if(userEntity == null) throw new AuthenticationException("error.authentication");
        return userEntity.getVideoPlayList();
    }

    @Override
    public List<CommentEntity> getAllCommentsByVideoId(Long videoId) {
        VideoEntity videoEntity = videoRepository.findById(videoId).orElseThrow(() -> new VideoNotFoundException("error.videoNotFound"));
        return videoEntity.getComments();
    }

    @Override
    public List<VideoEntity> getVideoByTitle(String title) {
        return videoRepository.findByTitleStartingWith(title);
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
    protected List<CommentEntity> getCommentsMedia(VideoEntity media) {
        return media.getComments();
    }

    @Override
    protected CommentEntity saveComment(VideoEntity media, CommentEntity comment) {
        CommentEntity commentEntity = commentRepository.save(comment);
        videoRepository.saveAndFlush(media);
        return commentEntity;
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
