package com.movie.celestix.features.media.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface MediaService {
    String store(MultipartFile file);

    void delete(String filename);

    String update(MultipartFile newFile, String publicId, String folderName);
}
