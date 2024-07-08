package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.validation.annotation.ImageExtension;
import com.myproject.bikereviewapp.validation.annotation.NotEmptyMultipartFile;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface ImageCloudService {

    String uploadImg(@Valid @NotEmptyMultipartFile @ImageExtension MultipartFile multipartFile, String folderName, String fileName);

    void deleteImg(String url);

}
