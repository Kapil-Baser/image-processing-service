package com.example.image_processing_service.transform.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FormatValidator.class)
@Documented
public @interface ValidFormat {
    String message() default "Format must be one of 'jpg', 'png', or 'webp";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
