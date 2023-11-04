package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Brand;

import java.util.List;

public interface BrandService {

    List<Brand> getAll();

    Brand getById(Long id);

    Brand create(Brand brand);

    Brand update(Long id, Brand brand);

    void delete(Long id);

}
