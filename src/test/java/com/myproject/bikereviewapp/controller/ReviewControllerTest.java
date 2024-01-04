package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    private static final String SAMPLE_VIEW = "some/view";

    @MockBean
    ReviewService reviewService;

    @MockBean
    UserService userService;

    @MockBean
    MotorcycleController motorcycleController;

    @Autowired
    ReviewController reviewController;


    private static Review review;
    private static User user;
    private static Authentication mockAuthentication;

    @BeforeAll
    static void init() {
        Motorcycle motorcycle = new Motorcycle();
        motorcycle.setId(10L);

        user = new User();
        user.setId(12L);
        user.setUsername("someUsername");

        review = new Review();
        review.setId(5L);
        review.setMotorcycle(motorcycle);
        review.setBody("somereview");

        mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(user.getUsername());
    }


    @Test
    void create_shouldCreateReview_ifUserIsAuthenticated() {

        BindingResult mockBindingResult = mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        reviewController.create(review, mockBindingResult, mockAuthentication, null, null, null, null);

        verify(reviewService).create(review);
    }

    @Test
    void create_shouldNeverCreateReview_ifUserIsNotAuthenticated() {

        BindingResult mockBindingResult = mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        assertThrows(UserIsNotAuthorizedException.class, () -> reviewController.create(review, mockBindingResult, null, null, null, null, null));

        verify(reviewService, never()).create(any());
    }

    @Test
    void create_shouldCreateReview_ifReviewIsValid() {

        BindingResult mockBindingResult = mock(BindingResult.class);
        MotorcycleController spyMotorcycleController = spy(motorcycleController);
        ReviewController spyReviewController = spy(new ReviewController(reviewService, userService, spyMotorcycleController));

        when(userService.getByUsername(user.getUsername())).thenReturn(user);

        when(mockBindingResult.hasErrors()).thenReturn(false);

        spyReviewController.create(review, mockBindingResult, mockAuthentication, null, null, null, null);

        verify(reviewService).create(review);
    }

    @Test
    void create_shouldNeverCreateReview_ifReviewIsInvalid() {

        BindingResult mockBindingResult = mock(BindingResult.class);
        MotorcycleController spyMotorcycleController = spy(motorcycleController);
        ReviewController spyReviewController = spy(new ReviewController(reviewService, userService, spyMotorcycleController));

        doReturn(SAMPLE_VIEW).when(spyMotorcycleController).show(any(), any(), any(), any(), any(), any());

        when(mockBindingResult.hasErrors()).thenReturn(true);

        spyReviewController.create(review, mockBindingResult, mockAuthentication, null, null, null, null);

        verify(reviewService, never()).create(any(Review.class));
    }
}