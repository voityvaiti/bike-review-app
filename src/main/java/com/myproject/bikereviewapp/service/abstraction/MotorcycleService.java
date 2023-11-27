package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Motorcycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MotorcycleService {

    Page<Motorcycle> getAll(Pageable pageable);

    Motorcycle getById(Long id);

    Motorcycle create(Motorcycle motorcycle);

    Motorcycle update(Long id, Motorcycle motorcycle);

    void delete(Long id);

}
