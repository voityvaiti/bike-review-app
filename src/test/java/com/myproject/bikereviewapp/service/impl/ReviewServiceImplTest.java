package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.repository.ReviewRepository;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

        Review review1 = new Review();
        review1.setId(2L);
        review1.setBody("somereview1");
        Review review2 = new Review();
        review1.setId(5L);
        review2.setBody("somereview2");

        List<Review> reviewList = List.of(
                review1, review2
        );

        when(motorcycleRepository.findById(id)).thenReturn(Optional.of(motorcycle));
        when(reviewRepository.findAllByMotorcycle(motorcycle)).thenReturn(reviewList);

        assertEquals(reviewList, reviewService.getReviewsByMotorcycleId(id));

        verify(motorcycleRepository).findById(id);
        verify(reviewRepository).findAllByMotorcycle(motorcycle);
    }

    @Test
    void getReviewsByMotorcycleId_shouldThrowException_whenMotorcycleWasNotFound() {
        Long id = 7L;

        when(motorcycleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reviewService.getReviewsByMotorcycleId(id));

        verify(motorcycleRepository).findById(id);
        verify(reviewRepository, never()).findAllByMotorcycle(any());
    }
}