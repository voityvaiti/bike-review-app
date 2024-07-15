package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Image;
import com.myproject.bikereviewapp.entity.pojo.FileUploadPojo;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.ImageRepository;
import com.myproject.bikereviewapp.service.abstraction.CloudService;
import com.myproject.bikereviewapp.service.abstraction.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    private final CloudService cloudService;


    @Override
    @Transactional
    public Image create(MultipartFile file, String folder) {

        FileUploadPojo fileUploadPojo =
                cloudService.upload(file, folder);

        Image image = new Image();
        image.setName(fileUploadPojo.getName());
        image.setFolder(folder);
        image.setUrl(fileUploadPojo.getUrl());

        return imageRepository.save(image);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Image image = getImageById(id);

        cloudService.delete(image.getFolder(), image.getName());
        imageRepository.delete(image);
    }


    private Image getImageById(Long id) {

        return imageRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Image with id " + id + " not found.")
        );
    }
}
