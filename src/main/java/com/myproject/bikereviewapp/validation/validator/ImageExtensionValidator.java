package com.myproject.bikereviewapp.validation.validator;

import com.myproject.bikereviewapp.validation.annotation.ImageExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


@Component
public class ImageExtensionValidator implements ConstraintValidator<ImageExtension, MultipartFile> {

    public static final List<String> ALLOWED_IMG_EXTENSIONS = Arrays.asList(".jpg", ".png");


    @Override
    public void initialize(ImageExtension constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {

        if (file == null || file.isEmpty()) {
            return true;
        }

        String fileName = file.getOriginalFilename();
        int lastDotIndex = file.getOriginalFilename().lastIndexOf('.');

        String extension = (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex);

        boolean isValid = ALLOWED_IMG_EXTENSIONS.contains(extension);

        if (!isValid) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "Only " + String.join(", ", ALLOWED_IMG_EXTENSIONS) + " extensions are allowed.")
                    .addConstraintViolation();
        }

        return isValid;
    }

}
