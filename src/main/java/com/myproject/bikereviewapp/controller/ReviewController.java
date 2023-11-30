package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/reviews")
public class ReviewController {

    final String USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE = "Error! User is not authorized.";

    protected static final String DEFAULT_REVIEWS_PAGE_NUMBER = "0";
    protected static final String DEFAULT_REVIEWS_PAGE_SIZE = "10";
    protected static final String DEFAULT_REVIEWS_SORT = "publicationDate:desc";

    private final ReviewService reviewService;
    private final UserService userService;
    private final MotorcycleController motorcycleController;

    public ReviewController(ReviewService reviewService, UserService userService, MotorcycleController motorcycleController) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.motorcycleController = motorcycleController;
    }


    @PostMapping
    public String create(@ModelAttribute("newReview") @Valid Review review,
                         BindingResult bindingResult, Authentication authentication, Model model,
                         @RequestParam(defaultValue = DEFAULT_REVIEWS_PAGE_NUMBER) Integer reviewPageNumber,
                         @RequestParam(defaultValue = DEFAULT_REVIEWS_PAGE_SIZE) Integer reviewPageSize,
                         @RequestParam(defaultValue = DEFAULT_REVIEWS_SORT) String reviewSort) {


        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        if (bindingResult.hasErrors()) {
            return motorcycleController.show(review.getMotorcycle().getId(), review, reviewPageNumber, reviewPageSize, reviewSort, model);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        review.setUser(userService.getByUsername(userDetails.getUsername()));
        reviewService.create(review);

        return "redirect:/motorcycles/" + review.getMotorcycle().getId();

    }
}
