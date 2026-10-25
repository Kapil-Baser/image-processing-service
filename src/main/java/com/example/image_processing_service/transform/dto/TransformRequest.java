package com.example.image_processing_service.transform.dto;

import jakarta.validation.Valid;

public record TransformRequest(@Valid Resize resize, @Valid Crop crop, @ValidRotation Integer rotate, Filter filters) {
}
