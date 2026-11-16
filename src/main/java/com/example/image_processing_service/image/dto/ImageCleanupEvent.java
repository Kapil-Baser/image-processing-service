package com.example.image_processing_service.image.dto;

import java.util.List;

public record ImageCleanupEvent(List<String> cacheKeys, List<String> paths) {
}
