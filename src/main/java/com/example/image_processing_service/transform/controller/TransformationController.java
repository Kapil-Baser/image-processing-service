package com.example.image_processing_service.transform.controller;

import com.example.image_processing_service.image.dto.ImageDto;
import com.example.image_processing_service.transform.dto.TransformRequest;
import com.example.image_processing_service.transform.service.TransformationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TransformationController {

    private final TransformationService transformationService;

    public TransformationController(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @PostMapping("/images/{id}/transform")
    public ResponseEntity<ImageDto> transform(@Valid @RequestBody TransformRequest request, @PathVariable("id") UUID id) {
        var response = transformationService.transformAndStore(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
