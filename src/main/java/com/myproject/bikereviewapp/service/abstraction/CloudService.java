package com.myproject.bikereviewapp.service.abstraction;

import org.springframework.web.multipart.MultipartFile;

public interface CloudService {

    String upload(MultipartFile multipartFile, String folderName, String fileName);

    void delete(String folderName, String fileName);

}
