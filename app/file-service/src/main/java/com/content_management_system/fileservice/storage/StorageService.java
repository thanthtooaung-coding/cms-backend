package com.content_management_system.fileservice.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface StorageService {

    void init();

    String store(MultipartFile file);

    List<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);

    void deleteAll();

    String update(MultipartFile newFile, String publicId, String folderName);
}