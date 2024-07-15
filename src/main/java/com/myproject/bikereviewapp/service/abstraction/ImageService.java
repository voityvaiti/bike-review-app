package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    Image create(MultipartFile file, String folder);

    void delete(Long id);
}
