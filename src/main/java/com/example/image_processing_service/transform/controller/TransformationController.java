package com.example.image_processing_service.transform.controller;

import com.example.image_processing_service.image.dto.ImageDto;
import com.example.image_processing_service.image.mapper.ImageMapper;
import com.example.image_processing_service.image.service.ImageOrchestrator;
import com.example.image_processing_service.transform.dto.TransformRequest;
import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TransformationController {

    private final ImageOrchestrator imageOrchestrator;
    private final UserService userService;

    @Autowired
    public TransformationController(ImageOrchestrator imageOrchestrator, UserService userService) {
        this.imageOrchestrator = imageOrchestrator;
        this.userService = userService;
    }

    @PostMapping("/images/{id}/transform")
    @Cacheable(cacheNames = "transformations", key = "#imageId + ':' + T(com.example.image_processing_service.core.utils.Utils).transformHash(#request)")
    public ResponseEntity<ImageDto> transform(@Valid @RequestBody TransformRequest request,
                                              @PathVariable("id") UUID imageId,
                                              @AuthenticationPrincipal Jwt jwt) {
        AppUser user = userService.getUserFromJWT(jwt);

        var response = imageOrchestrator.getOrCreateTransformation(user,imageId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ImageMapper.toDto(response));
    }
}
