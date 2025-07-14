package kg.sweezy.watchtime.service.impl;

import kg.sweezy.watchtime.entity.PreviewVideoEntity;
import kg.sweezy.watchtime.entity.VideoEntity;
import kg.sweezy.watchtime.exception.VideoPreviewUploadException;
import kg.sweezy.watchtime.exception.VideoUploadException;
import kg.sweezy.watchtime.service.MinIoService;
import kg.sweezy.watchtime.service.VideoMinIoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class VideoMinIoServiceImpl implements VideoMinIoService {
    private final MinIoService minIoService;
    @Value("${minio.bucket.name.video}")
    private String bucketNameVideo;
    @Value("${minio.bucket.name.previewVideo}")
    private String bucketNameVideoPreview;

    @Autowired
    public VideoMinIoServiceImpl(MinIoService minIoService) {
        this.minIoService = minIoService;
    }

    @Override
    public VideoEntity uploadVideo(VideoEntity videoEntity,MultipartFile videoFile) {
        if(videoFile != null && !videoFile.isEmpty()) {
            minIoService.upload(videoFile, bucketNameVideo);
            videoEntity.setFileName(videoFile.getOriginalFilename());
            return videoEntity;
        }
        throw new VideoUploadException("error.uploadVideo");
    }

    @Override
    public PreviewVideoEntity uploadVideoPreview(VideoEntity videoEntity,MultipartFile videoPreview) {
        if(videoPreview != null && !videoPreview.isEmpty()) {
            minIoService.upload(videoPreview, bucketNameVideoPreview);
            PreviewVideoEntity previewVideoEntity = PreviewVideoEntity.builder()
                    .fileName(videoPreview.getOriginalFilename())
                    .video(videoEntity)
                    .build();
            return previewVideoEntity;
        }
        throw new VideoPreviewUploadException("error.uploadVideoPreview");
    }

    @Override
    public InputStream getVideoByFileName(String fileName) {
        return minIoService.streamFile(bucketNameVideo, fileName);
    }

    @Override
    public InputStream getVideoPreviewByFileName(String fileName) {
        return minIoService.streamFile(bucketNameVideoPreview, fileName);
    }

    @Override
    public String getContentTypeVideo(String fileName) {
        return minIoService.getContent(bucketNameVideo, fileName);
    }

    @Override
    public String getContentTypeVideoPreview(String fileName) {
        return minIoService.getContent(bucketNameVideoPreview, fileName);
    }

    @Override
    public void deleteVideoByFileName(String fileName) {
        minIoService.deleteFile(bucketNameVideo, fileName);
    }

    @Override
    public void deleteVideoPreviewByFileName(String fileName) {
        minIoService.deleteFile(bucketNameVideoPreview, fileName);
    }

    @Override
    public Long getFileSizeByFileName(String fileName) {
        return minIoService.getFileSize(bucketNameVideo, fileName);
    }
}
