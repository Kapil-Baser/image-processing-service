package com.example.image_processing_service.transform.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class RotateValuesValidator implements ConstraintValidator<ValidRotation, Integer> {

    private static final Set<Integer> VALID_ANGLES = Set.of(90, 180, 270);

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        return VALID_ANGLES.contains(value);
    }
}
