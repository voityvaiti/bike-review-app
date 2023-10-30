package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Brand;

import java.util.List;

public interface BrandService {

    List<Brand> getAll();

    Brand create(Brand brand);

    void delete(Long id);

}
