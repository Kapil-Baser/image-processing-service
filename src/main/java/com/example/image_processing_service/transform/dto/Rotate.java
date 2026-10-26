package com.example.image_processing_service.transform.dto;

import jakarta.validation.constraints.NotNull;

public record Rotate(
        @NotNull(message = "Degrees cannot be empty.")
        @ValidRotation
        Integer degrees) {
}
