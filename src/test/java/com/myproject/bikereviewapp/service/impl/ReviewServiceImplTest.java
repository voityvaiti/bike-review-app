package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.entity.*;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.repository.MotorcycleRepository;
import com.myproject.bikereviewapp.repository.ReactionRepository;
import com.myproject.bikereviewapp.repository.ReviewRepository;
import com.myproject.bikereviewapp.repository.UserRepository;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import org.junit.jupiter.api.BeforeAll;
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
    ReviewRepository reviewRepository;
    @MockBean
    ReactionRepository reactionRepository;

    @MockBean
    MotorcycleRepository motorcycleRepository;

    @MockBean
    UserRepository userRepository;

    @Autowired
    ReviewService reviewService;


    private static final Long id = 7L;
    private static final Motorcycle motorcycle = new Motorcycle();
    private static final User user = new User();
    private static List<Review> sampleReviews;

    @BeforeAll
    static void init() {

        motorcycle.setId(id);
        motorcycle.setModel("someModel");
        motorcycle.setBrand(new Brand());

        user.setId(id);
        user.setUsername("someUsername");
        user.setPassword("somePassword");
        user.setEnabled(true);
        user.setRole(Role.CLIENT);
        user.setPublicName("somePublicName");

        Review review1 = new Review();
        review1.setId(2L);
        review1.setBody("somereview1");
        Review review2 = new Review();
        review1.setId(5L);
        review2.setBody("somereview2");

        sampleReviews = List.of(review1, review2);
    }

    @Test
    void getReviewsByMotorcycleId_shouldReturnReviewList_whenMotorcycleWasFound() {

        Pageable pageable = Mockito.mock(Pageable.class);

        Page<Review> reviewPage = new PageImpl<>(sampleReviews);

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

    @Test
    void getReviewsByUserId_shouldReturnReviewList_whenUserWasFound() {

        Pageable pageable = Mockito.mock(Pageable.class);

        Page<Review> reviewPage = new PageImpl<>(sampleReviews);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByUser(user, pageable)).thenReturn(reviewPage);

        assertEquals(reviewPage, reviewService.getReviewsByUserId(id, pageable));

        verify(userRepository).findById(id);
        verify(reviewRepository).findAllByUser(user, pageable);
    }

    @Test
    void getReviewsByUserId_shouldThrowException_whenUserWasNotFound() {
        Long id = 7L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reviewService.getReviewsByUserId(id, null));

        verify(userRepository).findById(id);
        verify(reviewRepository, never()).findAllByUser(any(), any());
    }
}