package com.example.image_processing_service.image.service;

import com.example.image_processing_service.image.dto.ImageDetailsResponse;
import com.example.image_processing_service.image.dto.ImageDto;
import com.example.image_processing_service.image.dto.ImageUploadResponse;
import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.image.exception.ImageNotFoundException;
import com.example.image_processing_service.image.repository.ImageRepository;
import com.example.image_processing_service.image.mapper.ImageMapper;
import com.example.image_processing_service.user.entity.AppUser;
import com.example.image_processing_service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageStorageService imageStorageService;
    private final UserService userService;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageStorageService imageStorageService, UserService userService, ImageRepository imageRepository) {
        this.imageStorageService = imageStorageService;
        this.userService = userService;
        this.imageRepository = imageRepository;
    }

    public ImageDetailsResponse uploadAndSave(Jwt jwt, MultipartFile file) {
        AppUser user = userService.getUserFromJWT(jwt);

        UUID imageID = UUID.randomUUID();

        String originalImageName = file.getOriginalFilename();

        ImageUploadResponse uploadResponse = imageStorageService.upload(imageID, user.getUsername(), file);

        Image image = ImageMapper.toImage(imageID, user, originalImageName, uploadResponse);

        Image saved = imageRepository.save(image);

        return ImageMapper.toImageDetailsRes(saved);
    }

    public Image getImage(UUID imageID) {
        return imageRepository.findById(imageID)
                .orElseThrow(() -> new ImageNotFoundException("Invalid UUID: " + imageID));
    }

    public List<ImageDto> getImages(Jwt jwt, Pageable pageable) {
        AppUser user = userService.getUserFromJWT(jwt);

        List<Image> images = imageRepository.getAllByUser(user, pageable);

        return images.stream()
                .map(ImageMapper::toDto)
                .toList();
    }

    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public void delete(UUID imageID) {
        imageRepository.deleteById(imageID);
    }

}
