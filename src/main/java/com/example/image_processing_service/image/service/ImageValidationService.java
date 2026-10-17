package com.example.image_processing_service.image.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ImageValidationService {

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private final Tika tika;

    public ImageValidationService(Tika tika) {
        this.tika = tika;
    }

    public boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return false;
        }

        // Verify magic bytes
        try {
            String mimeType = tika.detect(file.getInputStream());
            if (!ALLOWED_IMAGE_TYPES.contains(mimeType)) {
                log.info("Invalid mime type: {}", mimeType);
                return false;
            }

        } catch (IOException ex) {
            log.warn("Error reading file: {}", ex.getMessage());
            return false;
        }

        return true;
    }
}
