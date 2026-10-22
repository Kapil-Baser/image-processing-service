package com.example.image_processing_service.transform.dto;

import jakarta.validation.constraints.NotEmpty;

public record Rotate(
        @NotEmpty(message = "Degrees cannot be empty.")
        @ValidRotation
        Integer degrees) {
}
