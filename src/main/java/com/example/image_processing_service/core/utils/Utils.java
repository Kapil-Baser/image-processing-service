package com.example.image_processing_service.core.utils;

import com.example.image_processing_service.transform.dto.TransformRequest;
import org.apache.commons.codec.digest.DigestUtils;


public class Utils {
    private Utils() {}

    public static String transformHash(TransformRequest request) {
        String canonical = "v1|resize=%s|rotate=%s|flip=%s|mirror=%s|crop=%s|filters=%s".formatted(
                request.resize() == null ? "-" : request.resize().width() + "x" + request.resize().height(),
                request.rotate() == null ? "-" : request.rotate(),
                request.flip() == null ? "-" : request.flip(),
                request.mirror() == null ? "-" : request.mirror(),
                request.crop() == null ? "-" : request.crop().width() + "," + request.crop().height() + "," + request.crop().x() + "," + request.crop().y(),
                request.filters() == null ? "-" : request.filters().brightness() + "," + request.filters().grayscale());

        return DigestUtils.sha256Hex(canonical);
    }
}
