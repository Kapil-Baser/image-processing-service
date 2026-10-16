package com.example.image_processing_service.image.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ImageDto(UUID id,
                       String originalName,
                       String storagePath,
                       Long sizeBytes,
                       String mimeType,
                       Integer width,
                       Integer height,
                       LocalDateTime createdAt) {
}
