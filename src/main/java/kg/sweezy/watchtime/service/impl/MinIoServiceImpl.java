package kg.sweezy.watchtime.service.impl;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import kg.sweezy.watchtime.exception.FileIsNotExistException;
import kg.sweezy.watchtime.exception.UploadMultipartException;
import kg.sweezy.watchtime.service.MinIoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinIoServiceImpl implements MinIoService {
    private final MinioClient minioClient;

    @Autowired
    public MinIoServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void upload(MultipartFile file, String bucketName) {
        try{
            insureBucketExist(bucketName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                    .build()
            );
        } catch (Exception e) {
            throw new UploadMultipartException(e.getMessage());
        }
    }

    @Override
    public String getContent(String bucketName, String fileName) {
        try{
            StatObjectResponse statObjectResponse = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return statObjectResponse.contentType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream streamFile(String bucketName, String fileName) {
        try{
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insureBucketExist(String bucketName) {
        try{
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if(!found){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insureFileExist(String bucketName, String fileName) {
        try{
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            return true;
        }catch (ErrorResponseException e){
            if(e.errorResponse().code().equals("NoSuchKey")){
                return false;
            }else{
                throw new RuntimeException(e);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) {
        try{
            insureBucketExist(bucketName);
            if(insureFileExist(bucketName, fileName)){
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build()
                );
            }
        }catch (Exception e) {
            throw new FileIsNotExistException("error.fileDoesNotExist");
        }
    }

    @Override
    public Long getFileSize(String bucketName, String fileName) {
        try{
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
            return statObjectResponse.size();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
