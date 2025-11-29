package com.movie.celestix.features.media.service.impl;

import com.cloudinary.Cloudinary;
import com.movie.celestix.features.media.service.MediaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final Cloudinary cloudinary;

    @Override
    public String store(final MultipartFile file) {
        try {
            final Map<String, Object> options = new HashMap<>();
            options.put("folder", "food_ordering_system");
            @SuppressWarnings("unchecked")
            final Map<String, Object> uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
//            String publicId = (String) uploadedFile.get("public_id");
            return (String) uploadedFile.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to Cloudinary", e);
        }
    }

    @Override
    public void delete(final String publicId) {
        try {
            final Map<String, Object> deleteOptions = new HashMap<>();
            deleteOptions.put("invalidate", true);
            this.cloudinary.uploader().destroy(publicId, deleteOptions);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete from Cloudinary", e);
        }
    }

    @Override
    public String update(MultipartFile newFile, String publicId, String folderName) {
        try {
            final Map<String, Object> options = new HashMap<>();
            options.put("public_id", folderName + "/" + publicId);
            options.put("overwrite", true);

            @SuppressWarnings("unchecked")
            final Map<String, Object> uploadedFile = (Map<String, Object>) cloudinary.uploader().upload(newFile.getBytes(), options);
            return (String) uploadedFile.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image on Cloudinary", e);
        }
    }
}
