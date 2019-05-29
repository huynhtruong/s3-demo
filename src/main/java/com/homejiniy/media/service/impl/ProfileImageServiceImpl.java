package com.homejiniy.media.service.impl;

import com.homejiniy.media.service.ProfileImageService;
import lombok.extern.slf4j.Slf4j;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.springframework.stereotype.Service;

/**
 * @author Steven
 */
@Slf4j
@Service
public class ProfileImageServiceImpl implements ProfileImageService {

    private static final int THUMBNAIL_WIDTH = 100;
    @Override
    public String createThumbnail(String absPath, String uniqueName) throws MagickException {
    /** Typical scaling implementation using JMagick **/
        ImageInfo origInfo = new ImageInfo(absPath); //load image info
        MagickImage image = new MagickImage(origInfo); //load image
        int orgHeight = image.getDimension().height;
        int orgWidth = image.getDimension().width;
        image = image.scaleImage(THUMBNAIL_WIDTH, (THUMBNAIL_WIDTH *  orgHeight) / orgWidth); //to Scale image
        String extension = absPath.substring(absPath.lastIndexOf('.') + 1);
        String fileName = String.format("thumbnail-%s.%s", uniqueName, extension);
        image.setFileName(fileName); //give new location
        origInfo = new ImageInfo(fileName);
        image.writeImage(origInfo);
        return origInfo.getFileName();
    }
}
