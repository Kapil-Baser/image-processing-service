package com.example.image_processing_service.image.service;

import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.image.exception.ImageNotFoundException;
import com.example.image_processing_service.image.repository.ImageRepository;
import com.example.image_processing_service.user.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image getOwnedImage(AppUser user, UUID imageId) {
        return imageRepository.findByIdAndUser(imageId, user)
                .orElseThrow(() -> new ImageNotFoundException("Image not found: " + imageId));
    }

    public List<Image> getImages(AppUser user, Pageable pageable) {
        return imageRepository.getAllByUser(user, pageable);
    }

    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public void delete(Image image) {
        imageRepository.delete(image);
    }
}
