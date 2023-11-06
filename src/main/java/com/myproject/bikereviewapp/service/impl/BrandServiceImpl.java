package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.BrandRepository;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    @Override
    public List<Brand> getAllSortedByIdAsc() {
        return brandRepository.findAllByOrderByIdAsc();
    }


    @Override
    public Brand getById(Long id) {
        return brandRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Brand with id " + id + "not found")
        );
    }

    @Override
    public Brand create(Brand brand) {

        return brandRepository.save(brand);
    }

    @Override
    public Brand update(Long id, Brand updatedBrand) {

        Brand currentBrand = getById(id);

        currentBrand.setFields(updatedBrand);

        return brandRepository.save(currentBrand);
    }

    @Override
    public void delete(Long id) {
        brandRepository.delete(getById(id));
    }
}
