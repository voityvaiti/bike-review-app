package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.service.abstraction.CloudService;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class MotorcycleServiceImpl implements MotorcycleService {

    private static final String MOTORCYCLE_IMAGES_FOLDER = "motorcycle";


    private final MotorcycleRepository motorcycleRepository;

    private final CloudService cloudService;


    @Override
    public Page<Motorcycle> getAll(Pageable pageable) {

        log.debug("Page request received: {}", pageable);

        return motorcycleRepository.findAll(pageable);
    }

    @Override
    public Page<Motorcycle> getAllByQuery(String query, Pageable pageable) {

        log.debug("Page request received: {}, with query: {}", pageable, query);

        if(query != null && !query.isBlank()) {
            return motorcycleRepository.getAllByModelContainingIgnoreCaseOrBrandNameContainingIgnoreCase(query, query, pageable);
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Motorcycle getById(Long id) {

        return getMotorcycleById(id);
    }

    @Override
    public Motorcycle create(Motorcycle motorcycle) {

        log.debug("Saving new Motorcycle: {}", motorcycle);

        return motorcycleRepository.save(motorcycle);
    }

    @Override
    public Motorcycle update(Long id, Motorcycle updatedMotorcycle) {

        Motorcycle currentMotorcycle = getMotorcycleById(id);

        log.debug("Updating Motorcycle: {}", currentMotorcycle);

        currentMotorcycle.setBrand(updatedMotorcycle.getBrand());
        currentMotorcycle.setModel(updatedMotorcycle.getModel());

        log.debug("Saving updated Motorcycle: {}", currentMotorcycle);

        return motorcycleRepository.save(currentMotorcycle);
    }

    @Override
    public void uploadImg(Long id, MultipartFile file) {

        Motorcycle motorcycle = getMotorcycleById(id);

        if(motorcycle.getImgUrl() != null && !motorcycle.getImgUrl().isBlank()) {
            cloudService.delete(MOTORCYCLE_IMAGES_FOLDER, motorcycle.getId().toString());
        }

        motorcycle.setImgUrl(
                cloudService.upload(file, MOTORCYCLE_IMAGES_FOLDER, motorcycle.getId().toString())
        );
        motorcycleRepository.save(motorcycle);
    }

    @Override
    public void delete(Long id) {

        log.debug("Removing Motorcycle with ID: {}", id);

        motorcycleRepository.delete(getById(id));
    }


    private Motorcycle getMotorcycleById(Long id) {

        log.debug("Looking for Motorcycle with ID: {}", id);

        return motorcycleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Motorcycle with id " + id + " not found.")
        );
    }
}
