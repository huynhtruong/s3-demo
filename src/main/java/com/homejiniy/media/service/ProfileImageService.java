package com.homejiniy.media.service;

import magick.MagickException;

public interface ProfileImageService {
    String createThumbnail(String absPath, String uniqueName) throws MagickException;
}
