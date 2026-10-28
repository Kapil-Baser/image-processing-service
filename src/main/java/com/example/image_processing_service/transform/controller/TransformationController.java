package com.example.image_processing_service.transform.controller;

import com.example.image_processing_service.image.dto.ImageDto;
import com.example.image_processing_service.image.service.ImageOrchestrator;
import com.example.image_processing_service.transform.dto.TransformRequest;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TransformationController {

    private final ImageOrchestrator imageOrchestrator;

    public TransformationController(ImageOrchestrator imageOrchestrator) {
        this.imageOrchestrator = imageOrchestrator;
    }

    @PostMapping("/images/{id}/transform")
    @Cacheable(cacheNames = "transformations", key = "{#imageID, #request}")
    public ResponseEntity<ImageDto> transform(@Valid @RequestBody TransformRequest request, @PathVariable("id") UUID imageID) {

        var response = imageOrchestrator.getOrCreateTransformation(imageID, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
