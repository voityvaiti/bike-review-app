package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.BrandRepository;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.service.abstraction.ImageCloudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private static final String BRAND_IMAGES_FOLDER = "brand";


    private final BrandRepository brandRepository;

    private final ImageCloudService imageCloudService;


    @Override
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    @Override
    public Page<Brand> getAll(Pageable pageable) {

        log.debug("Page request received: {}", pageable);

        return brandRepository.findAll(pageable);
    }


    @Override
    public Brand getById(Long id) {

        return getBrandById(id);
    }

    @Override
    public Brand create(Brand brand) {

        log.debug("Saving new Brand: {}", brand);

        return brandRepository.save(brand);
    }

    @Override
    public Brand update(Long id, Brand updatedBrand) {

        Brand currentBrand = getBrandById(id);

        log.debug("Updating Brand: {}", currentBrand);

        currentBrand.setName(updatedBrand.getName());
        currentBrand.setCountry(updatedBrand.getCountry());

        log.debug("Saving updated Brand: {}", currentBrand);

        return brandRepository.save(currentBrand);
    }

    @Override
    public void uploadImg(Long id, MultipartFile file) {

        Brand brand = getBrandById(id);

        if(brand.getImgUrl() != null && !brand.getImgUrl().isBlank()) {
            imageCloudService.deleteImg(brand.getImgUrl());
        }

        brand.setImgUrl(
                imageCloudService.uploadImg(file, BRAND_IMAGES_FOLDER, brand.getId().toString())
        );
        brandRepository.save(brand);
    }

    @Override
    public void delete(Long id) {

        Brand brand = getById(id);

        log.debug("Removing Brand with ID: {}", id);

        brandRepository.delete(brand);
        imageCloudService.deleteImg(brand.getImgUrl());
    }


    private Brand getBrandById(Long id) {

        log.debug("Looking for Brand with ID: {}", id);

        return brandRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Brand with id " + id + "not found")
        );
    }
}
