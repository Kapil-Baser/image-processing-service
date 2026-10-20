package com.example.image_processing_service.image.dto;

import lombok.Builder;

@Builder
public record ImageDetailsResponse(String url, ImageMetadata metadata) {
}
