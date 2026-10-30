package com.example.image_processing_service.image.dto;

import java.awt.image.BufferedImage;
import java.util.UUID;

public record ImageUploadRequest(UUID imageId, String username, String format, BufferedImage bufferedImage) {
}
