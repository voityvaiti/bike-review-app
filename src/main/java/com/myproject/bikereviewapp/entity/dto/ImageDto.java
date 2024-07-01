package com.myproject.bikereviewapp.entity.dto;

import com.myproject.bikereviewapp.validation.annotation.ImageExtension;
import com.myproject.bikereviewapp.validation.annotation.NotEmptyMultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {

    @NotEmptyMultipartFile(message = "Image must be selected to upload.")
    @ImageExtension
    private MultipartFile image;

}
