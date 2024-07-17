package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Motorcycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MotorcycleService {

    Page<Motorcycle> getAll(Pageable pageable);

    Page<Motorcycle> getAllByQuery(String query, Pageable pageable);

    Motorcycle getById(Long id);

    Motorcycle create(Motorcycle motorcycle);

    Motorcycle update(Long id, Motorcycle motorcycle);

    void updateImg(Long id, MultipartFile file);

    void delete(Long id);

}
