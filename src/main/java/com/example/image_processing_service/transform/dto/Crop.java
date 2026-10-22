package com.example.image_processing_service.transform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Crop(

        @NotNull(message = "Width cannot be empty")
        @Min(value = 1, message = "Width must be at least 1 pixel")
        Integer width,

        @NotNull(message = "Height cannot be empty")
        @Min(value = 1, message = "Height must be at least 1 pixel")
        Integer height,

        @NotNull(message = "X coordinate cannot be empty")
        @Min(value = 1, message = "Crop x coordinate cannot be negative.")
        Integer x,

        @NotNull(message = "Y coordinate cannot be empty")
        @Min(value = 1, message = "Crop x coordinate cannot be negative.")
        Integer y) {
}
