package com.example.image_processing_service.transform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record Resize(
        @Min(value = 1, message = "Width must be at least 1 pixel")
        @Max(value = 4096, message = "Width cannot exceed 4096 pixels")
        Integer width,

        @Min(value = 1, message = "Height must be at least 1 pixel")
        @Max(value = 4096, message = "Height cannot exceed 4096 pixels")
        Integer height) {
}
