package com.example.image_processing_service.image.exception;

public class ImageDownloadFailedException extends RuntimeException {
    public ImageDownloadFailedException(String message) {
        super(message);
    }
}
