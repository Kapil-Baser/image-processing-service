package com.example.image_processing_service.transform.service;

import com.example.image_processing_service.transform.dto.Crop;
import com.example.image_processing_service.transform.dto.Filter;
import com.example.image_processing_service.transform.dto.Resize;
import com.example.image_processing_service.transform.dto.TransformRequest;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.image.BufferedImage;


@Service
public class TransformationService {

    public BufferedImage processTransformation(BufferedImage bufferedImage, TransformRequest request) {
        // Running all the transformations
        BufferedImage transformedImage;
        transformedImage = handleRotate(bufferedImage, request.rotate());

        transformedImage = handleFlip(transformedImage, request.flip());

        transformedImage = handleMirror(transformedImage, request.mirror());

        transformedImage = handleCrop(transformedImage, request.crop());

        transformedImage = handleResize(transformedImage, request.resize());

        transformedImage = processFilters(transformedImage, request.filters());

        transformedImage = handleConversion(transformedImage, request.format());

        bufferedImage.flush();

        return transformedImage;
    }

    private BufferedImage processFilters(BufferedImage originalImage, Filter filters) {
        if (Boolean.TRUE.equals(filters.brightness())) {
            originalImage = applyBrightnessFilter(originalImage);
        }

        if (Boolean.TRUE.equals(filters.grayscale())) {
            originalImage = applyGrayScaleFilter(originalImage);
        }

        return originalImage;
    }

    private BufferedImage handleRotate(BufferedImage originalImage, Integer degrees) {
        if (degrees == null) {
            return originalImage;
        }

        return switch (degrees) {
            case 90 -> Scalr.rotate(originalImage, Scalr.Rotation.CW_90, (java.awt.image.BufferedImageOp) null);

            case 180 -> Scalr.rotate(originalImage, Scalr.Rotation.CW_180, (java.awt.image.BufferedImageOp) null);

            case 270 -> Scalr.rotate(originalImage, Scalr.Rotation.CW_270, (java.awt.image.BufferedImageOp) null);

            default ->  originalImage;
        };
    }

    private BufferedImage handleFlip(BufferedImage originalImage, Boolean isFlipped) {
        if (Boolean.TRUE.equals(isFlipped)) {
            return Scalr.rotate(originalImage, Scalr.Rotation.FLIP_VERT, (java.awt.image.BufferedImageOp) null);
        }
        return originalImage;
    }

    private BufferedImage handleMirror(BufferedImage originalImage, Boolean isMirrored) {
        if (Boolean.TRUE.equals(isMirrored)) {
            return Scalr.rotate(originalImage, Scalr.Rotation.FLIP_HORZ, (java.awt.image.BufferedImageOp) null);
        }
        return originalImage;
    }

    private BufferedImage handleCrop(BufferedImage originalImage, Crop cropReq) {
        if (cropReq == null) {
            return originalImage;
        }

        return Scalr.crop(originalImage, cropReq.x(), cropReq.y(), cropReq.width(), cropReq.height(), (java.awt.image.BufferedImageOp) null);
    }

    private BufferedImage handleResize(BufferedImage originalImage, Resize resizeReq) {
        if (resizeReq == null || (resizeReq.height() == null && resizeReq.width() == null)) {
            return originalImage;
        }

        if (resizeReq.width() != null && resizeReq.height() != null) {

            return Scalr.resize(originalImage, Scalr.Mode.FIT_EXACT, resizeReq.width(), resizeReq.height(), (java.awt.image.BufferedImageOp) null);

        } else if (resizeReq.width() != null) {

            return Scalr.resize(originalImage, Scalr.Mode.AUTOMATIC, resizeReq.width());

        } else {

            return Scalr.resize(originalImage, Scalr.Mode.AUTOMATIC, resizeReq.height());

        }
    }

    private BufferedImage handleConversion(BufferedImage originalImage, String requestedFormat) {
        if (requestedFormat == null) {
            return originalImage;
        }

        BufferedImage finalImage = originalImage;

        if ("jpg".equals(requestedFormat) || "jpeg".equals(requestedFormat)) {
            finalImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g2d = finalImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, Color.WHITE, null);
            g2d.dispose();
        }

        return finalImage;
    }

    private BufferedImage applyBrightnessFilter(BufferedImage originalImage) {
        return Scalr.apply(originalImage, Scalr.OP_BRIGHTER);
    }

    private BufferedImage applyGrayScaleFilter(BufferedImage originalImage) {
        return Scalr.apply(originalImage, Scalr.OP_GRAYSCALE);
    }
}
