package kg.sweezy.watchtime.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinIoService {
    public void upload(MultipartFile file, String bucketName);
    public String getContent(String bucketName, String fileName);
    public InputStream streamFile(String bucketName, String fileName);
    public void insureBucketExist(String bucketName);
    public boolean insureFileExist(String bucketName, String fileName);
    public void deleteFile(String bucketName, String fileName);
    public Long getFileSize(String bucketName, String fileName);
}
