package com.example.image_processing_service.transform.repository;

import com.example.image_processing_service.transform.entity.Transformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransformationRepository extends JpaRepository<Transformation, UUID> {
    List<Transformation>  findAllBySourceImageId(UUID imageID);

    @NativeQuery("SELECT img.storage_path from app.images as img join app.transformations as tr on img.id = tr.result_image_id WHERE tr.source_image_id = :imageID")
    List<String> getStoragePathsByImage(UUID imageID);
}
