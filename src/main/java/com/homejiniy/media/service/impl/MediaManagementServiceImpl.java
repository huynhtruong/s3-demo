package com.homejiniy.media.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.homejiniy.media.config.ApplicationProperties;
import com.homejiniy.media.model.MediaModel;
import com.homejiniy.media.service.MediaManagementService;
import com.homejiniy.media.service.ProfileImageService;
import lombok.extern.slf4j.Slf4j;
import magick.MagickException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Steven
 */
@Slf4j
@Service
public class MediaManagementServiceImpl implements MediaManagementService {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ProfileImageService profileImageService;

    @Value("${cloud.aws.cloudfront.url}")
    private String mediaServerURL;

    /**
     * MediaModel is POJO object, need to call repository to save
     * @param files
     * @return
     */
    @Override
    public List<MediaModel> uploadMultipleFiles(List<MultipartFile> files) {
        List<MediaModel> resultList = new ArrayList();
        if (files != null) {
            files.forEach(multipartFile -> {
                MediaModel model = new MediaModel();
                File file = convertMultiPartFileToFile(multipartFile);
                UUID uuid = UUID.randomUUID();
                String extension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf('.') + 1);
                String uniqueFileName = uuid.toString() + "." + extension;
                uploadFileToS3bucket(uniqueFileName, file, applicationProperties.getAwsServices().getBucketName());
                model.setId(0l);
                model.setOriginalName(multipartFile.getOriginalFilename());
                model.setUrl(String.format("%s/%s", mediaServerURL, uniqueFileName));
                try {
                    model.setThumbnailUrl(String.format("%s/%s", mediaServerURL, createAndUploadThumbnail(file, uuid.toString())));
                } catch (MagickException e) {
                    e.printStackTrace();
                }
                file.delete();
                resultList.add(model);
            });
        }
        return resultList;
    }

    private String createAndUploadThumbnail(File file,  String uuid) throws MagickException {
        String fileName = profileImageService.createThumbnail(file.getAbsolutePath(), uuid.toString());
        File thumbnail = new File(fileName);
        uploadFileToS3bucket(fileName, thumbnail, applicationProperties.getAwsServices().getBucketName());
        thumbnail.delete();
        return fileName;
    }

    private void uploadFileToS3bucket(String fileName, File file, String bucketName) {
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
    }


    private S3Object downloadFileFromS3bucket(String fileName, String bucketName) {
        S3Object object = amazonS3Client.getObject(bucketName,  fileName);
        return object;

    }

    private void deleteFileFromS3bucket(String fileName, String bucketName) {
        amazonS3Client.deleteObject(bucketName, fileName);
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
