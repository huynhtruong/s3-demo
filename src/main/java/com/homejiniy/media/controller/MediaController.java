package com.homejiniy.media.controller;

import com.homejiniy.media.model.MediaModel;
import com.homejiniy.media.service.MediaManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Steven
 */
@RestController
public class MediaController {

    @Autowired
    private MediaManagementService mediaManagementService;

    @PostMapping("${app.endpoint.uploadFiles}")
    public List<MediaModel> uploadMultipleFiles(@ModelAttribute List<MultipartFile> files) {
        return mediaManagementService.uploadMultipleFiles(files);
    }
}