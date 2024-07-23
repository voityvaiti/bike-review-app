package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.BrandRepository;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.service.abstraction.ImageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BrandServiceImpl.class)
class BrandServiceImplTest {
    
    @MockBean
    BrandRepository brandRepository;

    @MockBean
    ImageService imageService;


    @Autowired
    BrandService brandService;

    private static final Long id = 10L;
    private static final Brand brand = new Brand();
    private static final Brand updatedBrand =  new Brand();


    @BeforeAll
    static void init() {

        brand.setId(id);
        brand.setName("someName");
        brand.setCountry("someCountry");

        updatedBrand.setId(id);
        updatedBrand.setName("updatedName");
        updatedBrand.setCountry("updatedCountry");

    }

    @Test
    void getById_shouldReturnBrand_whenBrandWasFound() {

        when(brandRepository.findById(id)).thenReturn(Optional.of(brand));

        assertEquals(brand, brandService.getById(id));

        verify(brandRepository).findById(id);
    }

    @Test
    void getById_shouldThrowException_whenBrandWasNotFound() {

        when(brandRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.getById(id));

        verify(brandRepository).findById(id);
    }

    @Test
    void update_shouldSaveAndReturnUpdatedBrand() {

        when(brandRepository.findById(id)).thenReturn(Optional.of(brand));
        when(brandRepository.save(updatedBrand)).thenReturn(updatedBrand);

        assertEquals(updatedBrand, brandService.update(id, updatedBrand));

        verify(brandRepository).save(updatedBrand);
    }

    @Test
    void update_shouldSaveAndReturnUpdatedBrand_whenBrandWasReceivedWithoutId() {

        when(brandRepository.findById(id)).thenReturn(Optional.of(brand));
        when(brandRepository.save(brand)).thenReturn(updatedBrand);

        assertEquals(updatedBrand, brandService.update(id, brand));

        verify(brandRepository).save(brand);
    }

}