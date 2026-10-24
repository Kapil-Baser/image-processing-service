package com.example.image_processing_service.image.mapper;

import com.example.image_processing_service.image.dto.ImageDetailsResponse;
import com.example.image_processing_service.image.dto.ImageDto;
import com.example.image_processing_service.image.dto.ImageMetadata;
import com.example.image_processing_service.image.dto.ImageUploadResponse;
import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.transform.entity.Transformation;
import com.example.image_processing_service.user.entity.AppUser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ImageMapper {

    public Image toEntity(UUID imageID, AppUser user, ImageDetailsResponse response) {
        return Image.builder()
                .id(imageID)
                .originalName(response.metadata().name())
                .storagePath(response.url())
                .sizeBytes(response.metadata().sizeBytes())
                .mimeType(response.metadata().mimeType())
                .height(response.metadata().height())
                .width(response.metadata().width())
                .user(user)
                .build();
    }

    public static Image toImage(UUID imageID, AppUser user, String originalImageName, ImageUploadResponse response) {
        return Image.builder()
                .id(imageID)
                .originalName(originalImageName)
                .storagePath(response.storagePath())
                .sizeBytes(response.sizeBytes())
                .mimeType(response.mime())
                .height(response.height())
                .width(response.width())
                .user(user)
                .build();
    }

    public static ImageDto toDto(Image image) {
        return ImageDto.builder()
                .id(image.getId())
                .originalName(image.getOriginalName())
                .storagePath(image.getStoragePath())
                .sizeBytes(image.getSizeBytes())
                .mimeType(image.getMimeType())
                .height(image.getHeight())
                .width(image.getWidth())
                .createdAt(image.getCreatedAt())
                .build();
    }

    public static ImageDetailsResponse toImageDetailsRes(Image image) {
        return ImageDetailsResponse.builder()
                .url(image.getStoragePath())
                .metadata(
                        new ImageMetadata(
                                image.getOriginalName(),
                                image.getMimeType(),
                                image.getSizeBytes(),
                                image.getHeight(),
                                image.getWidth())
                )
                .build();
    }

    public static Transformation toTransformation(UUID transformationID, AppUser user, Image originalImage, Image resultImage) {
        return Transformation.builder()
                .id(transformationID)
                .user(user)
                .sourceImage(originalImage)
                .resultImage(resultImage)
                .build();
    }
}
