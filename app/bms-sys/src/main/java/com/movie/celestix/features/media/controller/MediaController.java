package com.movie.celestix.features.media.controller;

import com.movie.celestix.common.dto.ApiResponse;
import com.movie.celestix.features.media.dto.MediaUrlResponse;
import com.movie.celestix.features.media.service.MediaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
@AllArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<ApiResponse<MediaUrlResponse>> storeMedia(
            @RequestParam("file") final MultipartFile file
    ) {
        try {
            final String fileUrl = this.mediaService.store(file);
            return ApiResponse.ok(new MediaUrlResponse(fileUrl), "Media uploaded successfully");
        } catch (Exception e) {
            return ApiResponse.conflict("Failed to upload media file");
        }
    }
}
