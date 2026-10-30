package com.example.image_processing_service.image.controller;

import com.example.image_processing_service.image.dto.ImageDetailsResponse;
import com.example.image_processing_service.image.dto.ImageDto;
import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.image.mapper.ImageMapper;
import com.example.image_processing_service.image.service.ImageOrchestrator;
import com.example.image_processing_service.image.service.ImageService;
import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
public class ImageController {
    private final ImageService imageService;
    private final ImageOrchestrator imageOrchestrator;
    private final UserService userService;

    public ImageController(ImageService imageService, ImageOrchestrator imageOrchestrator, UserService userService) {
        this.imageService = imageService;
        this.imageOrchestrator = imageOrchestrator;
        this.userService = userService;
    }

    @PostMapping( path = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDetailsResponse> uploadImage(@RequestParam("image") MultipartFile image, @AuthenticationPrincipal Jwt jwt) {
        AppUser user = userService.getUserFromJWT(jwt);
        Image saved = imageOrchestrator.upload(user, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ImageMapper.toImageDetailsRes(saved));
    }

    @GetMapping("/images")
    public ResponseEntity<List<ImageDto>> getImages(Pageable pageable,
                                                    @AuthenticationPrincipal Jwt jwt) {
        AppUser user = userService.getUserFromJWT(jwt);
        var images = imageService.getImages(user, pageable);
        return ResponseEntity.ok(images.stream().map(ImageMapper::toDto).toList());
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable("id") UUID imageId, @AuthenticationPrincipal Jwt jwt) {
        AppUser user = userService.getUserFromJWT(jwt);
        imageOrchestrator.delete(user, imageId);
        return ResponseEntity.noContent().build();
    }
}
