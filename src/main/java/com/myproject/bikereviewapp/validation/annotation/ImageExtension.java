package com.myproject.bikereviewapp.validation.annotation;

import com.myproject.bikereviewapp.validation.validator.ImageExtensionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageExtensionValidator.class})
public @interface ImageExtension {

    String message() default "Invalid image extension.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
