package com.example.image_processing_service.image.service;

import com.example.image_processing_service.image.dto.ImageUploadRequest;
import com.example.image_processing_service.image.dto.ImageUploadResponse;
import com.example.image_processing_service.image.exception.ImageDownloadFailedException;
import com.example.image_processing_service.image.exception.ImageNotFoundException;
import com.example.image_processing_service.image.exception.ImageUploadFailedException;
import com.example.image_processing_service.image.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageService {

    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final ImageValidationService imageValidator;
    private final RestClient storageClient;
    private final Tika tika;
    private final String baseUrl;
    private final String bucket;

    @Autowired
    public ImageStorageService(ImageValidationService imageValidationService,
                               RestClient storageClient, Tika tika,
                               @Value("${supabase.s3.endpoint}")
                               String baseUrl,
                               @Value("${supabase.s3.bucket}")
                               String bucket) {
        this.imageValidator = imageValidationService;
        this.storageClient = storageClient;
        this.tika = tika;
        this.baseUrl = baseUrl;
        this.bucket = bucket;
    }

    public ImageUploadResponse upload(UUID imageID, String username, MultipartFile file) {
        if (!imageValidator.isImageFile(file)) {
            throw new InvalidFileException("Invalid file, only images are allowed.");
        }

        String storagePath;
        ImageUploadResponse response;

        try {
            byte[] bytes = file.getBytes();

            String mime = tika.detect(bytes);
            String ext = ALLOWED.get(mime);

            String fileName = imageID + "." + ext;

            storageClient.post()
                .uri("/object/{bucket}/{folder}/{file}", bucket, username, fileName)
                .body(bytes)
                .contentType(MediaType.parseMediaType(mime))
                .retrieve()
                .toBodilessEntity();

            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            storagePath = String.format("%s/object/%s/%s/%s", baseUrl, bucket, username, fileName);

            response = new ImageUploadResponse(
                    storagePath,
                    file.getSize(),
                    mime,
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight()
            );

        } catch (IOException e) {
            log.warn("Error while uploading image: {}", e.getMessage());
            throw new ImageUploadFailedException("Image upload failed.");
        }

        return response;
    }

    public ImageUploadResponse uploadTransformation(ImageUploadRequest uploadRequest) {

        String storagePath;
        ImageUploadResponse response;
        String username = uploadRequest.username();
        String mime = "image/" + uploadRequest.format();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(uploadRequest.bufferedImage(), uploadRequest.format(), baos);

            byte[] bytes = baos.toByteArray();

            long contentLength = bytes.length;

            String fileName = uploadRequest.imageId() + "." + uploadRequest.format();


            storageClient.post()
                    .uri("/object/{bucket}/{folder}/transform/{fileName}", bucket, username, fileName)
                    .body(bytes)
                    .contentLength(contentLength)
                    .contentType(MediaType.parseMediaType(mime))
                    .retrieve()
                    .toBodilessEntity();

            storagePath = String.format("%s/object/%s/%s/transform/%s", baseUrl, bucket, username, fileName);

            response = new ImageUploadResponse(storagePath,
                    contentLength,
                    mime,
                    uploadRequest.bufferedImage().getWidth(),
                    uploadRequest.bufferedImage().getHeight());

        } catch (IOException e) {
            log.warn("Error while uploading transformed image: {}", e.getMessage());
            throw new ImageUploadFailedException("Image upload failed.");
        }

        return response;
    }

    public BufferedImage download(String url) {
        try {
            Resource resource = storageClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Resource.class);

            if (resource == null) {
                throw new ImageNotFoundException("Received empty body.");
            }

            try (InputStream inputStream = resource.getInputStream()) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image == null) {
                    throw new InvalidFileException("Invalid image format");
                }
                return image;
            }
        } catch (IOException e) {
            throw new ImageDownloadFailedException("Failed to download the image." + e.getMessage());
        }
    }

    public void deleteImage(String storagePath) {
        var response = storageClient.delete()
                .uri(storagePath)
                .retrieve()
                .body(String.class);
        log.info(response);
    }
}
