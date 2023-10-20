package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import com.myproject.bikereviewapp.service.impl.MotorcycleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @MockBean
    ReviewService reviewService;

    @MockBean
    UserService userService;

    @Autowired
    ReviewController reviewController;


    @Test
    void create_shouldCreateReview_ifUserIsAuthenticated() {

        Long motorcycleId = 10L;
        Motorcycle motorcycle = new Motorcycle();
        motorcycle.setId(motorcycleId);

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        User user = new User();
        user.setUsername("testUser");

        Long reviewId = 5L;
        Review review = new Review();
        review.setId(reviewId);
        review.setMotorcycle(motorcycle);
        review.setBody("somereview");

        review.setUser(user);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getUsername());

        assertEquals("redirect:/motorcycles/" + motorcycleId, reviewController.create(review, authentication));

        verify(reviewService).create(review);
    }

    @Test
    void create_shouldThrowException_ifUserIsNotAuthenticated() {

        Review review = new Review();

        assertThrows(UserIsNotAuthorizedException.class, () -> reviewController.create(review, null));

        verify(reviewService, never()).create(any());
    }
}