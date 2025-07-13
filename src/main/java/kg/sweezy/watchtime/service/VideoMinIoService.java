package kg.sweezy.watchtime.service;

import kg.sweezy.watchtime.entity.PreviewVideoEntity;
import kg.sweezy.watchtime.entity.VideoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface VideoMinIoService {
    public VideoEntity uploadVideo(VideoEntity videoEntity,MultipartFile videoFile);
    public PreviewVideoEntity uploadVideoPreview(VideoEntity videoEntity,MultipartFile videoPreview);
    public InputStream getVideoByFileName(String fileName);
    public InputStream getVideoPreviewByFileName(String fileName);
    public String getContentTypeVideo(String fileName);
    public String getContentTypeVideoPreview(String fileName);
    public void deleteVideoByFileName(String fileName);
    public void deleteVideoPreviewByFileName(String fileName);
    public Long getFileSizeByFileName(String fileName);
}
