package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotorcycleServiceImpl implements MotorcycleService {

    private final MotorcycleRepository motorcycleRepository;

    public MotorcycleServiceImpl(MotorcycleRepository motorcycleRepository) {
        this.motorcycleRepository = motorcycleRepository;
    }


    @Override
    public List<Motorcycle> getAll() {
        return motorcycleRepository.findAll();
    }

    @Override
    public Motorcycle getById(Long id) {
        Optional<Motorcycle> optionalMotorcycle = motorcycleRepository.findById(id);

        return optionalMotorcycle.orElseThrow(()
                -> new EntityNotFoundException("Motorcycle with id " + id + " not found."));
    }

    @Override
    public Motorcycle create(Motorcycle motorcycle) {

        return motorcycleRepository.save(motorcycle);
    }

    @Override
    public void delete(Long id) {

        motorcycleRepository.deleteById(id);
    }
}
