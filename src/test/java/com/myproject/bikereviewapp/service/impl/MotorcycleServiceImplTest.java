package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MotorcycleServiceImpl.class)
class MotorcycleServiceImplTest {

    @MockBean
    MotorcycleRepository motorcycleRepository;

    @Autowired
    MotorcycleService motorcycleService;

    @Test
    void getById_shouldReturnMotorcycle_whenMotorcycleWasFound() {
        Long id = 7L;
        Motorcycle motorcycle = new Motorcycle(id, "someModel", new Brand());

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
}