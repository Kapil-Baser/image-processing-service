package com.example.image_processing_service.image.event;

import com.example.image_processing_service.image.dto.ImageCleanupEvent;
import com.example.image_processing_service.image.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Slf4j
public class StorageCleanupEventListener {

    private final CacheManager cacheManager;
    private final ImageStorageService storageService;

    @Autowired
    public StorageCleanupEventListener(CacheManager cacheManager, ImageStorageService storageService) {
        this.cacheManager = cacheManager;
        this.storageService = storageService;
    }

    @Async("imageExecutor")
    @TransactionalEventListener
    public void handleCleanup(ImageCleanupEvent event) {
        log.info("Task executing from: {}", Thread.currentThread().getName());
        List<String> cacheKeys = event.cacheKeys();
        List<String> paths = event.paths();

        Cache cache = cacheManager.getCache("transformations");
        if (cache != null) {
            cacheKeys.forEach(cache::evict);
            paths.forEach(storageService::deleteImage);
        }
    }
}
