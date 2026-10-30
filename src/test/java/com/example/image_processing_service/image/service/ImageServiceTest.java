package com.example.image_processing_service.image.service;

import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.image.exception.ImageNotFoundException;
import com.example.image_processing_service.image.repository.ImageRepository;
import com.example.image_processing_service.user.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository repository;

    @InjectMocks
    private ImageService imageService;

    @Test
    void getOwnedImage_returnsImage_whenOwnedByUser() {
        AppUser user = new AppUser();
        UUID imageId = UUID.randomUUID();
        Image image = Image.builder()
                .user(user)
                .id(imageId)
                .build();
        when(repository.findByIdAndUser(imageId, user)).thenReturn(Optional.of(image));

        Image expectedImage = imageService.getOwnedImage(user, imageId);

        assertThat(image.getUser()).isEqualTo(expectedImage.getUser());
        assertThat(image.getId()).isEqualTo(expectedImage.getId());
    }

    @Test
    void getOwnedImage_throwsNotFound_whenMissingOrNotOwned() {
        AppUser user = new AppUser();
        UUID imageId = UUID.randomUUID();

        when(repository.findByIdAndUser(imageId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.getOwnedImage(user, imageId))
                .isInstanceOf(ImageNotFoundException.class)
                .hasMessageContaining(imageId.toString());
    }
}