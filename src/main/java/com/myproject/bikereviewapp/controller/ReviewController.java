package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Reaction;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.myproject.bikereviewapp.controller.UserController.USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE;


@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    protected static final String DEFAULT_REVIEWS_PAGE_NUMBER = "0";
    protected static final String DEFAULT_REVIEWS_SORT = "publicationDate:desc";
    public static final Integer REVIEWS_PAGE_SIZE = 10;

    protected static final String REVIEW_PAGE_ATTR = "reviewPage";
    protected static final String NEW_REVIEW_ATTR = "newReview";

    private static final String SHOW_MOTORCYCLE_REDIRECT = "redirect:/motorcycles/{id}";


    private final ReviewService reviewService;
    private final UserService userService;



    @PostMapping
    public String create(@ModelAttribute(NEW_REVIEW_ATTR) @Valid Review review,
                         BindingResult bindingResult, Authentication authentication,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(required = false) Integer reviewPageNumber,
                         @RequestParam(required = false) String reviewSort) {


        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(NEW_REVIEW_ATTR, review);

            redirectAttributes.addAttribute("id", review.getMotorcycle().getId());
            redirectAttributes.addAttribute("reviewPageNumber", reviewPageNumber);
            redirectAttributes.addAttribute("reviewSort", reviewSort);
            return SHOW_MOTORCYCLE_REDIRECT;
        }

        review.setUser(userService.getByUsername(authentication.getName()));
        reviewService.create(review);

        redirectAttributes.addAttribute("id", review.getMotorcycle().getId());
        return SHOW_MOTORCYCLE_REDIRECT;

    }

    @PatchMapping("/reaction")
    public String addReaction(@RequestParam Long reviewId, @RequestParam boolean isLike,
                              Authentication authentication, RedirectAttributes redirectAttributes,
                              @RequestParam(required = false) Integer reviewPageNumber,
                              @RequestParam(required = false) String reviewSort) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        Review reactionReview = reviewService.getById(reviewId);
        User reactionUser = userService.getByUsername(authentication.getName());

        reviewService.saveReaction(new Reaction(null, isLike, reactionReview, reactionUser));

        redirectAttributes.addAttribute("id", reactionReview.getMotorcycle().getId());
        redirectAttributes.addAttribute("reviewPageNumber", reviewPageNumber);
        redirectAttributes.addAttribute("reviewSort", reviewSort);
        return SHOW_MOTORCYCLE_REDIRECT;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, @RequestParam Long motorcycleId, RedirectAttributes redirectAttributes) {

        reviewService.delete(id);

        redirectAttributes.addAttribute("id", motorcycleId);
        return SHOW_MOTORCYCLE_REDIRECT;
    }
}
