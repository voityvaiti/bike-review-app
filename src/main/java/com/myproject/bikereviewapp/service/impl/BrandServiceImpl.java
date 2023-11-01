package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
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
    public Brand create(Brand brand) {

        return brandRepository.save(brand);
    }

    @Override
    public void delete(Long id) {

        brandRepository.deleteById(id);
    }
}
