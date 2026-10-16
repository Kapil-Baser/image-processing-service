package com.example.image_processing_service.image.repository;

import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.user.entity.AppUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    Optional<Image> findById(UUID imageID);

    List<Image> getAllByUser(AppUser user, Pageable pageable);
}
