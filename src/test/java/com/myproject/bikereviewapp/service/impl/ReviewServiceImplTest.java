package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.repository.ReviewRepository;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ReviewServiceImpl.class)
class ReviewServiceImplTest {

    @MockBean
    MotorcycleRepository motorcycleRepository;

    @MockBean
    ReviewRepository reviewRepository;

    @Autowired
    ReviewService reviewService;


    @Test
    void getReviewsByMotorcycleId_shouldReturnReviewList_whenMotorcycleWasFound() {
        Long id = 7L;
        Motorcycle motorcycle = new Motorcycle(id, "someModel", new Brand());

        Pageable pageable = Mockito.mock(Pageable.class);

        Review review1 = new Review();
        review1.setId(2L);
        review1.setBody("somereview1");
        Review review2 = new Review();
        review1.setId(5L);
        review2.setBody("somereview2");

        Page<Review> reviewPage = new PageImpl<>(List.of(
                review1, review2
        ));

        when(motorcycleRepository.findById(id)).thenReturn(Optional.of(motorcycle));
        when(reviewRepository.findAllByMotorcycle(motorcycle, pageable)).thenReturn(reviewPage);

        assertEquals(reviewPage, reviewService.getReviewsByMotorcycleId(id, pageable));

        verify(motorcycleRepository).findById(id);
        verify(reviewRepository).findAllByMotorcycle(motorcycle, pageable);
    }

    @Test
    void getReviewsByMotorcycleId_shouldThrowException_whenMotorcycleWasNotFound() {
        Long id = 7L;

        when(motorcycleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reviewService.getReviewsByMotorcycleId(id, null));

        verify(motorcycleRepository).findById(id);
        verify(reviewRepository, never()).findAllByMotorcycle(any(), any());
    }
}