package com.example.image_processing_service.image.service;

import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.transform.entity.Transformation;
import com.example.image_processing_service.transform.repository.TransformationRepository;
import com.example.image_processing_service.transform.service.TransformationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ImageOrchestrator {

    private final ImageService imageService;
    private final TransformationService transformationService;
    private final ImageStorageService storageService;
    private final TransformationRepository transformationRepository;

    public ImageOrchestrator(ImageService imageService, TransformationService transformationService, ImageStorageService storageService, TransformationRepository transformationRepository) {
        this.imageService = imageService;
        this.transformationService = transformationService;
        this.storageService = storageService;
        this.transformationRepository = transformationRepository;
    }

    @Transactional
    public void deleteImageAndCleanupStorage(UUID imageID) {
        // Pull the image from DB
        Image image = imageService.getImage(imageID);

        String storagePath = image.getStoragePath();

        // Get a list of storage paths for transformation on this image
        List<String> transformedImagesStoragePaths = transformationRepository.getStoragePathsByImage(imageID);

        List<Transformation> transformations = transformationRepository.findAllBySourceImageId(imageID);

        // Can just use SQL query here
        List<UUID> transformedImagesIDs = transformationRepository.getIDsOfAllTransformationsByImage(imageID);

        // remove all the transformations from remote bucket
        transformedImagesStoragePaths.forEach(storageService::deleteImage);

        // First deleting the transformations from DB
        transformations.forEach(transformationRepository::delete);

        // Deleting the transformed images
        transformedImagesIDs.forEach(imageService::delete);

        // delete image from bucket
        storageService.deleteImage(storagePath);

        // delete the image from DB
        imageService.delete(imageID);
    }
}
