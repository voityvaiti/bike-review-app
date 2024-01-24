package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.BrandRepository;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandServiceImpl.class);

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    @Override
    public Page<Brand> getAll(Pageable pageable) {

        LOGGER.debug("Page request received: {}", pageable);

        return brandRepository.findAll(pageable);
    }


    @Override
    public Brand getById(Long id) {

        LOGGER.debug("Looking for Brand with ID: {}", id);

        return brandRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Brand with id " + id + "not found")
        );
    }

    @Override
    public Brand create(Brand brand) {

        LOGGER.debug("Saving new Brand: {}", brand);

        return brandRepository.save(brand);
    }

    @Override
    public Brand update(Long id, Brand updatedBrand) {

        Brand currentBrand = getById(id);

        LOGGER.debug("Updating Brand: {}", currentBrand);

        currentBrand.setName(updatedBrand.getName());
        currentBrand.setCountry(updatedBrand.getCountry());

        LOGGER.debug("Saving updated Brand: {}", currentBrand);

        return brandRepository.save(currentBrand);
    }

    @Override
    public void delete(Long id) {

        LOGGER.debug("Removing Brand with ID: {}", id);

        brandRepository.delete(getById(id));
    }
}
