package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BrandService {

    List<Brand> getAll();

    Page<Brand> getAll(Pageable pageable);

    Brand getById(Long id);

    Brand create(Brand brand);

    Brand update(Long id, Brand brand);

    void updateImg(Long id, MultipartFile file);

    void delete(Long id);

}
