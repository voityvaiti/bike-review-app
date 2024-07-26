package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.BrandRepository;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.service.abstraction.ImageService;
import jakarta.transaction.Transactional;
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

    private final ImageService imageService;


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
    @Transactional
    public void updateImg(Long id, MultipartFile file) {

        Brand brand = getBrandById(id);

        if(brand.getImage() != null) {
            imageService.delete(brand.getImage().getId());
        }

        brand.setImage(
                imageService.create(file, BRAND_IMAGES_FOLDER)
        );
        brandRepository.save(brand);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Brand brand = getBrandById(id);

        log.debug("Removing Brand with ID: {}", id);

        imageService.delete(brand.getImage().getId());
        brandRepository.delete(getById(id));
    }


    private Brand getBrandById(Long id) {

        log.debug("Looking for Brand with ID: {}", id);

        return brandRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Brand with id " + id + "not found")
        );
    }
}
