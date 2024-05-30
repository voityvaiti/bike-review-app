package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Reaction;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.EntityNotFoundException;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.myproject.bikereviewapp.controller.MainController.BINDING_RESULT_ATTR;
import static com.myproject.bikereviewapp.controller.MainController.ID;
import static com.myproject.bikereviewapp.controller.UserController.USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE;


@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    protected static final String DEFAULT_REVIEWS_PAGE_NUMBER = "0";
    protected static final String DEFAULT_REVIEWS_SORT = "publicationDate:desc";
    protected static final Integer REVIEW_PAGE_SIZE = 10;

    protected static final String REVIEW_PAGE_ATTR = "reviewPage";
    protected static final String NEW_REVIEW_ATTR = "newReview";
    protected static final String REVIEW_PAGE_NUMBER_ATTR = "reviewPageNumber";
    protected static final String REVIEW_SORT_ATTR = "reviewSort";

    private static final String SHOW_MOTORCYCLE_REDIRECT = "redirect:/motorcycles/{id}";


    private final ReviewService reviewService;
    private final UserService userService;



    @PostMapping
    public String create(@ModelAttribute(NEW_REVIEW_ATTR) @Valid Review review,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @RequestParam(required = false, name = REVIEW_PAGE_NUMBER_ATTR) Integer reviewPageNumber,
                         @RequestParam(required = false, name = REVIEW_SORT_ATTR) String reviewSort) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        if (bindingResult.hasErrors()) {

            if(review.getMotorcycle() == null) {
                throw new EntityNotFoundException("Undefined motorcycle.");
            }

            redirectAttributes.addFlashAttribute(NEW_REVIEW_ATTR, review);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + NEW_REVIEW_ATTR, bindingResult);

            redirectAttributes.addAttribute(ID, review.getMotorcycle().getId());
            redirectAttributes.addAttribute(REVIEW_PAGE_NUMBER_ATTR, reviewPageNumber);
            redirectAttributes.addAttribute(REVIEW_SORT_ATTR, reviewSort);

        } else {
            review.setUser(userService.getByUsername(authentication.getName()));
            reviewService.create(review);

            redirectAttributes.addAttribute(ID, review.getMotorcycle().getId());
        }

        return SHOW_MOTORCYCLE_REDIRECT;
    }

    @PatchMapping("/reaction")
    public String addReaction(@RequestParam Long reviewId, @RequestParam boolean isLike,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(required = false, name = REVIEW_PAGE_NUMBER_ATTR) Integer reviewPageNumber,
                              @RequestParam(required = false, name = REVIEW_SORT_ATTR) String reviewSort) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        Review reactionReview = reviewService.getById(reviewId);
        User reactionUser = userService.getByUsername(authentication.getName());

        reviewService.saveReaction(new Reaction(null, isLike, reactionReview, reactionUser));

        redirectAttributes.addAttribute(ID, reactionReview.getMotorcycle().getId());
        redirectAttributes.addAttribute(REVIEW_PAGE_NUMBER_ATTR, reviewPageNumber);
        redirectAttributes.addAttribute(REVIEW_SORT_ATTR, reviewSort);
        return SHOW_MOTORCYCLE_REDIRECT;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable(ID) Long id, @RequestParam Long motorcycleId, RedirectAttributes redirectAttributes) {

        reviewService.delete(id);

        redirectAttributes.addAttribute(ID, motorcycleId);
        return SHOW_MOTORCYCLE_REDIRECT;
    }
}
