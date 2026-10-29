package com.example.image_processing_service.transform.repository;

import com.example.image_processing_service.transform.entity.Transformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransformationRepository extends JpaRepository<Transformation, UUID> {
    List<Transformation>  findAllBySourceImageId(UUID imageID);

    @NativeQuery("""
    SELECT img.storage_path FROM app.images AS img
    JOIN app.transformations AS tr
    ON img.id = tr.result_image_id
    WHERE tr.source_image_id = :imageID
    """)
    List<String> getStoragePathsByImage(UUID imageID);

    @NativeQuery("""
    SELECT img.id FROM app.images AS img
    JOIN app.transformations AS tr
    ON img.id = tr.result_image_id
    WHERE tr.source_image_id = :imageID
    """)
    List<UUID> getIDsOfAllTransformationsByImage(UUID imageID);

    @NativeQuery("SELECT * FROM app.transformations AS tr WHERE tr.source_image_id = :sourceId AND tr.transform_hash = :transformHash")
    Optional<Transformation> findBySourceImageAndTransformHash(UUID sourceId, String transformHash);
}
