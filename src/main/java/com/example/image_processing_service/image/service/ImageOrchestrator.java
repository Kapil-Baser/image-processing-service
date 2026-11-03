package com.example.image_processing_service.image.service;

import com.example.image_processing_service.core.utils.Utils;
import com.example.image_processing_service.image.dto.ImageUploadRequest;
import com.example.image_processing_service.image.dto.ImageUploadResponse;
import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.image.mapper.ImageMapper;
import com.example.image_processing_service.transform.dto.TransformRequest;
import com.example.image_processing_service.transform.entity.Transformation;
import com.example.image_processing_service.transform.repository.TransformationRepository;
import com.example.image_processing_service.transform.service.TransformationService;
import com.example.image_processing_service.user.entity.AppUser;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.*;

@Component
public class ImageOrchestrator {

    private static final Map<String, String> IMAGE_EXT = Map.of(
            "jpg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp");

    private final ImageService imageService;
    private final TransformationService transformationService;
    private final ImageStorageService storageService;
    private final TransformationRepository transformationRepository;
    private final CacheManager cacheManager;

    public ImageOrchestrator(ImageService imageService, TransformationService transformationService, ImageStorageService storageService, TransformationRepository transformationRepository, CacheManager cacheManager) {
        this.imageService = imageService;
        this.transformationService = transformationService;
        this.storageService = storageService;
        this.transformationRepository = transformationRepository;
        this.cacheManager = cacheManager;
    }

    public Image getOrCreateTransformation(AppUser user, UUID sourceId, TransformRequest req) {
        Image source = imageService.getOwnedImage(user, sourceId);
        String hash = Utils.transformHash(req);

        return transformationRepository.findBySourceImageAndTransformHash(sourceId, hash)
                .orElseGet(() -> transformAndStore(user, source, hash, req))
                .getResultImage();
    }

    public Transformation transformAndStore(AppUser user, Image source, String hash, TransformRequest req) {
        BufferedImage input = storageService.download(source.getStoragePath());
        BufferedImage output = transformationService.processTransformation(input, req);

        UUID resultId = UUID.randomUUID();
        String format = req.format() == null ? IMAGE_EXT.get(source.getMimeType()) : req.format().toLowerCase();

        ImageUploadResponse upload = storageService.uploadTransformation(
                new ImageUploadRequest(resultId, user.getUsername(), format, output));


        Image result = imageService.save(ImageMapper.toImage(resultId, user, source.getOriginalName(), upload));

        Transformation transformation = ImageMapper.toTransformation(UUID.randomUUID(), user, source, result);
        transformation.setTransformHash(hash);

        return transformationRepository.save(transformation);
    }

    public Image upload(AppUser user, MultipartFile file) {
        UUID imageId = UUID.randomUUID();
        ImageUploadResponse uploadResponse = storageService.upload(imageId, user.getUsername(), file);
        Image image = ImageMapper.toImage(imageId, user, file.getOriginalFilename(), uploadResponse);

        try {
            return imageService.save(image);
        } catch (RuntimeException e) {
            storageService.deleteImage(image.getStoragePath());
            throw e;
        }
    }

    @Transactional
    public void delete(AppUser user, UUID imageId) {
        Image image = imageService.getOwnedImage(user, imageId);

        Cache cache = cacheManager.getCache("transformations");

        // Find the transformations where this image is the source.
        List<Transformation> asSource = transformationRepository.findAllBySourceImageId(imageId);
        // Find the transformation that produced this image.
        Optional<Transformation> asResult = transformationRepository.findByResultImageId(imageId);

        List<Transformation> toDelete = new ArrayList<>(asSource);
        asResult.ifPresent(toDelete::add);

        List<Image> imagesToDelete = new ArrayList<>();
        imagesToDelete.add(image);
        asSource.forEach(t -> imagesToDelete.add(t.getResultImage()));

        List<String> paths = imagesToDelete.stream()
                .map(Image::getStoragePath)
                .toList();

        List<String> cacheKeys = toDelete.stream()
                .map(t -> t.getSourceImage().getId() + ":" + t.getTransformHash())
                .toList();

        transformationRepository.deleteAll(toDelete);
        transformationRepository.flush();
        imagesToDelete.forEach(imageService::delete);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (cache != null) {
                    cacheKeys.forEach(cache::evict);
                    paths.forEach(storageService::deleteImage);
                }
            }
        });
    }
}
