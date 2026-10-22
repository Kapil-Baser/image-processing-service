package com.example.image_processing_service.transform.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RotateValuesValidator.class)
@Documented
public @interface ValidRotation {
    String message() default "Rotation angle must be exactly 90, 180, or 270 degrees.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
