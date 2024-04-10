package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Reaction;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.myproject.bikereviewapp.controller.ReviewController.NEW_REVIEW_ATTR;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {


    @MockBean
    ReviewService reviewService;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReviewController reviewController;


    private static Review review;


    @BeforeAll
    static void init() {

        Motorcycle motorcycle = new Motorcycle();
        motorcycle.setId(10L);

        User user = new User();
        user.setId(12L);
        user.setUsername("someUsername");

        review = new Review();
        review.setId(5L);
        review.setMotorcycle(motorcycle);
        review.setBody("somereview");
        review.setPublicationDate(LocalDate.now());
        review.setRating((short) 4);
    }


    @Test
    @WithMockUser
    void create_shouldCreateReviewAndRedirect_ifUserIsAuthenticatedAndReviewIsValid() throws Exception {

        mockMvc.perform(post("/reviews")
                        .flashAttr(NEW_REVIEW_ATTR, review))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/motorcycles/" + review.getMotorcycle().getId()));

        verify(reviewService).create(review);
    }

    @Test
    void create_shouldReturn401AndNeverCreateReview_ifUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(post("/reviews")
                        .flashAttr(NEW_REVIEW_ATTR, review))
                .andExpect(status().isUnauthorized());

        verify(reviewService, never()).create(any());
    }

    @Test
    @WithMockUser
    void create_shouldRedirectAndNeverCreateReview_ifReviewIsInvalid() throws Exception {

        Motorcycle motorcycle = new Motorcycle();
        motorcycle.setId(1L);

        Review invalidReview = new Review();
        invalidReview.setMotorcycle(motorcycle);
        invalidReview.setRating((short) 634);


        mockMvc.perform(post("/reviews")
                        .flashAttr(NEW_REVIEW_ATTR, invalidReview))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/motorcycles/" + invalidReview.getMotorcycle().getId()));

        verify(reviewService, never()).create(any());
    }

    @Test
    @WithMockUser(username = "test-user")
    void addReaction_shouldSaveReactionAndRedirect_ifUserIsAuthenticated() throws Exception {

        User user = new User();
        user.setId(5L);
        user.setUsername("test-user");

        boolean isLike = true;

        when(reviewService.getById(review.getId())).thenReturn(review);
        when(userService.getByUsername(user.getUsername())).thenReturn(user);

        mockMvc.perform(patch("/reviews/reaction")
                .param("reviewId", String.valueOf(review.getId()))
                .param("isLike", String.valueOf(isLike)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/motorcycles/" + review.getMotorcycle().getId()));

        verify(reviewService).saveReaction(new Reaction(null, isLike, review, user));
    }

    @Test
    void addReaction_shouldReturn401AndNeverSaveReaction_ifUserIsNotAuthenticated() throws Exception {

        boolean isLike = true;

        mockMvc.perform(patch("/reviews/reaction")
                        .param("reviewId", String.valueOf(review.getId()))
                        .param("isLike", String.valueOf(isLike)))
                .andExpect(status().isUnauthorized());

        verify(reviewService, never()).saveReaction(any());

    }
}
