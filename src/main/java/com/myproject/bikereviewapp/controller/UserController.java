package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.entity.dto.PasswordUpdateDto;
import com.myproject.bikereviewapp.entity.dto.PublicNameUpdateDto;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import com.myproject.bikereviewapp.utility.SortUtility;
import jakarta.validation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.myproject.bikereviewapp.controller.ReviewController.*;

@Controller
@RequestMapping("/users")
public class UserController {

    protected static final String USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE = "Error! User is not authorized.";

    private static final String REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL = "redirect:/users/admin";

    private static final String PASSWORD_EDIT_PAGE = "user/password-edit";

    private final UserService userService;

    private final ReviewService reviewService;

    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model,
                                      @RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "20") Integer pageSize,
                                      @RequestParam(defaultValue = "id:asc") String sort) {

        model.addAttribute("userPage", userService.getAll(PageRequest.of(pageNumber, pageSize, SortUtility.parseSort(sort))));

        model.addAttribute("currentPageNumber", pageNumber);
        model.addAttribute("currentSort", sort);

        return "user/admin/all";
    }

    @GetMapping("/profile")
    public String showCurrentUserProfile(Authentication authentication, Model model,
                                         @RequestParam(defaultValue = DEFAULT_REVIEWS_PAGE_NUMBER) Integer reviewPageNumber,
                                         @RequestParam(defaultValue = DEFAULT_REVIEWS_PAGE_SIZE) Integer reviewPageSize,
                                         @RequestParam(defaultValue = DEFAULT_REVIEWS_SORT) String reviewSort) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.getByUsername(userDetails.getUsername());

        model.addAttribute("user", currentUser);

        model.addAttribute("reviewPage", reviewService.getReviewsByUserId(currentUser.getId(), PageRequest.of(reviewPageNumber, reviewPageSize, SortUtility.parseSort(reviewSort))));
        model.addAttribute("currentReviewPageNumber", reviewPageNumber);
        model.addAttribute("currentReviewSort", reviewSort);

        return "user/profile";
    }

    @GetMapping("/admin/new")
    public String newUser(@ModelAttribute User user, Model model) {
        model.addAttribute("roles", Role.values());
        return "user/admin/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {

        //Temp solution to check username for uniqueness
        if(userService.exists(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "User with same username is already exists.");
        }

        if(bindingResult.hasErrors()) {
            return newUser(user, model);
        }
        userService.create(user);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @PatchMapping ("/admin/is-enabled/{id}")
    public String toggleStatus(@PathVariable Long id) {

        userService.toggleStatus(id);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }



    @GetMapping("/profile/password-edit")
    public String editCurrentUserPassword(Model model) {

        model.addAttribute("passwordUpdateDto", new PasswordUpdateDto());
        return PASSWORD_EDIT_PAGE;
    }

    @PatchMapping("/password")
    public String updateCurrentUserPassword(@ModelAttribute PasswordUpdateDto passwordUpdateDto, Authentication authentication, BindingResult bindingResult) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }
        String username = authentication.getName();


        if (!userService.isCorrectCredentials(username, passwordUpdateDto.getOldPassword())) {
            bindingResult.rejectValue("oldPassword", "passwordUpdateDto.oldPassword", "Wrong old password.");
        }
        User currentUser = userService.getByUsername(username);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<User>> violations = validator.validateValue(User.class, "password", passwordUpdateDto.getNewPassword());

            violations.forEach(violation ->
                    bindingResult.rejectValue("newPassword", "passwordUpdateDto.newPassword", violation.getMessage())
            );
        }

        if(bindingResult.hasErrors()) {
            return PASSWORD_EDIT_PAGE;
        }


        userService.updatePassword(currentUser.getId(), passwordUpdateDto.getNewPassword());

        return "redirect:/users/profile";
    }


    @GetMapping("/profile/public-name-edit")
    public String editCurrentUserPublicName(Model model, Authentication authentication) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }
        User currentUser = userService.getByUsername(authentication.getName());

        model.addAttribute("publicNameUpdateDto", new PublicNameUpdateDto(currentUser.getPublicName()));
        return "user/public-name-edit";
    }

    @PatchMapping("/public-name")
    public String updateCurrentUserPublicName(@ModelAttribute @Valid PublicNameUpdateDto publicNameUpdateDto, BindingResult bindingResult, Authentication authentication) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }
        if (bindingResult.hasErrors()) {
            return "user/public-name-edit";
        }

        User currentUser = userService.getByUsername(authentication.getName());

        userService.updatePublicName(currentUser.getId(), publicNameUpdateDto.getPublicName());

        return "redirect:/users/profile";
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable Long id) {

        userService.delete(id);
        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

}
