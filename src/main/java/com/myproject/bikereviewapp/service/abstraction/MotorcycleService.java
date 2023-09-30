package com.myproject.bikereviewapp.service.abstraction;

import com.myproject.bikereviewapp.entity.Motorcycle;

public interface MotorcycleService {

    Motorcycle create(Motorcycle motorcycle);

    void delete(Long id);

}
