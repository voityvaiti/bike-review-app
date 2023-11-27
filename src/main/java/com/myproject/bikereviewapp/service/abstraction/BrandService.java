package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {

    List<Brand> getAll();

    Page<Brand> getAll(Pageable pageable);

    Brand getById(Long id);

    Brand create(Brand brand);

    Brand update(Long id, Brand brand);

    void delete(Long id);

}
