package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class MotorcycleServiceImpl implements MotorcycleService {

    private final MotorcycleRepository motorcycleRepository;

    public MotorcycleServiceImpl(MotorcycleRepository motorcycleRepository) {
        this.motorcycleRepository = motorcycleRepository;
    }


    @Override
    public Page<Motorcycle> getAll(Pageable pageable) {
        return motorcycleRepository.findAll(pageable);
    }

    @Override
    public Page<Motorcycle> getAllByQuery(String query, Pageable pageable) {

        if(query != null && !query.isBlank()) {
            return motorcycleRepository.getAllByModelContainingIgnoreCaseOrBrandNameContainingIgnoreCase(query, query, pageable);
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Motorcycle getById(Long id) {
        return motorcycleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Motorcycle with id " + id + " not found.")
        );
    }

    @Override
    public Motorcycle create(Motorcycle motorcycle) {

        return motorcycleRepository.save(motorcycle);
    }

    @Override
    public Motorcycle update(Long id, Motorcycle updatedMotorcycle) {

        Motorcycle currentMotorcycle = getById(id);

        currentMotorcycle.setFields(updatedMotorcycle);

        return motorcycleRepository.save(currentMotorcycle);
    }

    @Override
    public void delete(Long id) {
        motorcycleRepository.delete(getById(id));
    }
}
