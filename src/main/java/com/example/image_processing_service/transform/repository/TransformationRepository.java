package com.example.image_processing_service.transform.repository;

import com.example.image_processing_service.transform.entity.Transformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransformationRepository extends JpaRepository<Transformation, UUID> {
}
