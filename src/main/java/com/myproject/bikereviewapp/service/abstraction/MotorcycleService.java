package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Motorcycle;

import java.util.List;

public interface MotorcycleService {

    List<Motorcycle> getAll();

    Motorcycle create(Motorcycle motorcycle);

    void delete(Long id);

}
