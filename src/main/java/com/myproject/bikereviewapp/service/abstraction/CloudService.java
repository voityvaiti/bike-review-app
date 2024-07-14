package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.pojo.FileUploadPojo;
import com.myproject.bikereviewapp.validation.annotation.NotEmptyMultipartFile;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface CloudService {

    FileUploadPojo upload(@Valid @NotEmptyMultipartFile MultipartFile multipartFile, String folderName);

    void delete(String folderName, String fileName);

}
