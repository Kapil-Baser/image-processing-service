package com.example.image_processing_service.transform.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class FormatValidator implements ConstraintValidator<ValidFormat, String> {

    private static final Set<String> VALID_FORMATS = Set.of("jpg", "png", "webp");

    @Override
    public boolean isValid(String format, ConstraintValidatorContext constraintValidatorContext) {
        if (format == null) {
            return true;
        }
        return VALID_FORMATS.contains(format);
    }
}
