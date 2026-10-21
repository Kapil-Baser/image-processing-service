package com.example.image_processing_service.image.controller;

import com.example.image_processing_service.image.dto.ImageDetailsResponse;
import com.example.image_processing_service.image.dto.ImageDto;
import com.example.image_processing_service.image.service.ImageService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
public class ImageController {


    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping( path = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDetailsResponse> uploadImage(@RequestParam("image") MultipartFile image, @AuthenticationPrincipal Jwt jwt) {
        var response = imageService.uploadAndSave(jwt, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/images")
    public ResponseEntity<List<ImageDto>> getImages(Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        var images = imageService.getImages(jwt, pageable);
        return ResponseEntity.ok(images);
    }
}
