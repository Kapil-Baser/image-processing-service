package com.example.image_processing_service.image.dto;

public record ImageMetadata(String name, String mimeType, Long sizeBytes, Integer height, Integer width) {
}
