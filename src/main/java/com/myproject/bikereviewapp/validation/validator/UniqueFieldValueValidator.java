package com.myproject.bikereviewapp.validation.validator;


import com.myproject.bikereviewapp.service.abstraction.EntityService;
import com.myproject.bikereviewapp.validation.annotation.UniqueFieldValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueFieldValueValidator implements ConstraintValidator<UniqueFieldValue, Object> {

    private final EntityService entityService;
    private Class<?> entityClass;
    private String fieldName;

    public UniqueFieldValueValidator(EntityService entityService) {
        this.entityService = entityService;
    }

    @Override
    public void initialize(UniqueFieldValue constraintAnnotation) {
        this.entityClass = constraintAnnotation.entityClass();
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return !entityService.exists(entityClass, fieldName, value);
    }
}
