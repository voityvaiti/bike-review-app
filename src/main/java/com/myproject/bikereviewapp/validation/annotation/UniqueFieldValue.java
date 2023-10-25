package com.myproject.bikereviewapp.validation.annotation;
import com.myproject.bikereviewapp.validation.validator.UniqueFieldValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueFieldValueValidator.class)
public @interface UniqueFieldValue {

    String message() default "Value duplication.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entityClass();

    String fieldName();
}
