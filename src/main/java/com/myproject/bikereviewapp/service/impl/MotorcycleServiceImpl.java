package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MotorcycleServiceImpl implements MotorcycleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotorcycleServiceImpl.class);

    private final MotorcycleRepository motorcycleRepository;


    @Override
    public Page<Motorcycle> getAll(Pageable pageable) {

        LOGGER.debug("Page request received: {}", pageable);

        return motorcycleRepository.findAll(pageable);
    }

    @Override
    public Page<Motorcycle> getAllByQuery(String query, Pageable pageable) {

        LOGGER.debug("Page request received: {}, with query: {}", pageable, query);

        if(query != null && !query.isBlank()) {
            return motorcycleRepository.getAllByModelContainingIgnoreCaseOrBrandNameContainingIgnoreCase(query, query, pageable);
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Motorcycle getById(Long id) {

        LOGGER.debug("Looking for Motorcycle with ID: {}", id);

        return motorcycleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Motorcycle with id " + id + " not found.")
        );
    }

    @Override
    public Motorcycle create(Motorcycle motorcycle) {

        LOGGER.debug("Saving new Motorcycle: {}", motorcycle);

        return motorcycleRepository.save(motorcycle);
    }

    @Override
    public Motorcycle update(Long id, Motorcycle updatedMotorcycle) {

        Motorcycle currentMotorcycle = getById(id);

        LOGGER.debug("Updating Motorcycle: {}", currentMotorcycle);

        currentMotorcycle.setBrand(updatedMotorcycle.getBrand());
        currentMotorcycle.setModel(updatedMotorcycle.getModel());

        LOGGER.debug("Saving updated Motorcycle: {}", currentMotorcycle);

        return motorcycleRepository.save(currentMotorcycle);
    }

    @Override
    public void delete(Long id) {

        LOGGER.debug("Removing Motorcycle with ID: {}", id);

        motorcycleRepository.delete(getById(id));
    }
}
