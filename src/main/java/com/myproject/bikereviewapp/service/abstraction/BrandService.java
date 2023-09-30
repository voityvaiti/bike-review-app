package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Brand;

public interface BrandService {

    Brand create(Brand brand);

    void delete(Long id);

}
