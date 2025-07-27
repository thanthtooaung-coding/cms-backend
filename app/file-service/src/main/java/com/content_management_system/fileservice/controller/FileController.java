package com.content_management_system.fileservice.controller;

import com.content_management_system.fileservice.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = storageService.store(file);
        return new ResponseEntity<>(Map.of("url", url), HttpStatus.OK);
    }
}
