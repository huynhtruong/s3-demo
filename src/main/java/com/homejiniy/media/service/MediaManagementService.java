package com.homejiniy.media.service;

import com.homejiniy.media.model.MediaModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Steven
 */
public interface MediaManagementService {
    List<MediaModel> uploadMultipleFiles(List<MultipartFile> multipartFiles);
}
