package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.service.abstraction.CloudService;
import com.myproject.bikereviewapp.service.abstraction.ImageService;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MotorcycleServiceImpl.class)
class MotorcycleServiceImplTest {

    @MockBean
    MotorcycleRepository motorcycleRepository;

    @MockBean
    ImageService imageService;


    @Autowired
    MotorcycleService motorcycleService;


    private static final Long id = 7L;
    private static final Motorcycle motorcycle = new Motorcycle();

    @BeforeAll
    static void init() {

        motorcycle.setId(id);
        motorcycle.setModel("someModel");
        motorcycle.setBrand(new Brand());
    }

    @Test
    void getById_shouldReturnMotorcycle_whenMotorcycleWasFound() {

        when(motorcycleRepository.findById(id)).thenReturn(Optional.of(motorcycle));

        assertEquals(motorcycle, motorcycleService.getById(id));

        verify(motorcycleRepository).findById(id);
    }

    @Test
    void getById_shouldThrowException_whenMotorcycleWasNotFound() {
        Long id = 7L;

        when(motorcycleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> motorcycleService.getById(id));

        verify(motorcycleRepository).findById(id);
    }

    @Test
    void getAllByQuery_shouldReturnPage_whenQueryIsNotBlank() {
        String query = "Honda";
        Pageable pageable = PageRequest.of(0, 10);
        List<Motorcycle> motorcycles = Arrays.asList(
                motorcycle,
                new Motorcycle()
        );
        Page<Motorcycle> expectedPage = new PageImpl<>(motorcycles, pageable, motorcycles.size());

        when(motorcycleRepository.getAllByModelContainingIgnoreCaseOrBrandNameContainingIgnoreCase(query, query, pageable)).thenReturn(expectedPage);

        assertEquals(expectedPage, motorcycleService.getAllByQuery(query, pageable));

        verify(motorcycleRepository)
                .getAllByModelContainingIgnoreCaseOrBrandNameContainingIgnoreCase(query, query, pageable);
    }

    @Test
    void getAllByQuery_shouldReturnEmptyPage_whenQueryIsBlank() {
        String blankQuery = "";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Motorcycle> emptyPage = new PageImpl<>(Collections.emptyList());

        assertEquals(emptyPage, motorcycleService.getAllByQuery(blankQuery, pageable));

        verify(motorcycleRepository, never())
                .getAllByModelContainingIgnoreCaseOrBrandNameContainingIgnoreCase(any(), any(), any());
    }

    @Test
    void getAllByQuery_shouldReturnEmptyPage_whenQueryIsNull() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Motorcycle> emptyPage = new PageImpl<>(Collections.emptyList());

        assertEquals(emptyPage, motorcycleService.getAllByQuery(null, pageable));

        verify(motorcycleRepository, never())
                .getAllByModelContainingIgnoreCaseOrBrandNameContainingIgnoreCase(any(), any(), any());
    }
}