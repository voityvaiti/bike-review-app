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
@RequestMapping("/review")
public class ReviewController {

    private final String USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE = "Error! User is not authorized.";

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
                         BindingResult bindingResult, Authentication authentication, Model model) {


        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        if (bindingResult.hasErrors()) {
            return motorcycleController.show(review.getMotorcycle().getId(), review, model);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        review.setUser(userService.findByUsername(userDetails.getUsername()));
        reviewService.create(review);

        return "redirect:/motorcycles/" + review.getMotorcycle().getId();

    }
}
