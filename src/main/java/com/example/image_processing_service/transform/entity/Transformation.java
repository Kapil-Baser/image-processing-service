package com.example.image_processing_service.transform.entity;

import com.example.image_processing_service.image.entity.Image;
import com.example.image_processing_service.user.entity.AppUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transformations")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Transformation {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_image_id", nullable = false)
    private Image sourceImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_image_id")
    private Image resultImage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
