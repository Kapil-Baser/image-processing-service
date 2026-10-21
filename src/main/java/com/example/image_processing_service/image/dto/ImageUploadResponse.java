package com.example.image_processing_service.image.dto;

public record ImageUploadResponse(String storagePath, long sizeBytes, String mime, int width, int height) {
}
