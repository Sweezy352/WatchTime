package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.VideoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService extends MediaBaseService{
    public VideoEntity uploadVideo(VideoEntity videoEntity, MultipartFile videoFile, MultipartFile videoPreview);
    public VideoEntity getVideoById(Long id);
    public List<VideoEntity> getAllVideos();
    public List<VideoEntity> getAllVideosByChannel(Long channelId);
    public String deleteVideo(Long id);
}
