package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.entity.dto.ImageDto;
import com.myproject.bikereviewapp.entity.dto.PasswordUpdateDto;
import com.myproject.bikereviewapp.entity.dto.PublicNameUpdateDto;
import com.myproject.bikereviewapp.entity.dto.RoleUpdateDto;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import com.myproject.bikereviewapp.utility.SortUtility;
import com.myproject.bikereviewapp.validation.validator.UserUniquenessValidator;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.myproject.bikereviewapp.controller.MainController.*;
import static com.myproject.bikereviewapp.controller.ReviewController.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    protected static final String USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE = "Error! User is not authorized.";

    protected static final String USER_PAGE_ATTR = "userPage";
    protected static final String USER_ATTR = "user";
    protected static final String ROLE_LIST_ATTR = "roles";

    protected static final String PASSWORD_UPDATE_DTO_ATTR = "passwordUpdateDto";
    protected static final String OLD_PASSWORD_FIELD_ATTR = "oldPassword";
    protected static final String PUBLIC_NAME_UPDATE_DTO_ATTR = "publicNameUpdateDto";
    protected static final String ROLE_UPDATE_DTO_ATTR = "roleUpdateDto";


    private static final String PASSWORD_EDIT_PAGE = "user/password-edit";
    private static final String REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL = "redirect:/users/admin";


    private final UserService userService;
    private final ReviewService reviewService;

    private final UserUniquenessValidator uniquenessValidator;
    private final SortUtility sortUtility;



    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER, name = PAGE_NUMBER_ATTR) Integer pageNumber,
                                      @RequestParam(defaultValue = DEFAULT_ADMIN_PAGE_SIZE, name = PAGE_SIZE_ATTR) Integer pageSize,
                                      @RequestParam(defaultValue = DEFAULT_ADMIN_PAGE_SORT, name = SORT_ATTR) String sort) {

        model.addAttribute(USER_PAGE_ATTR, userService.getAll(PageRequest.of(pageNumber, pageSize, sortUtility.parseSort(sort, User.class))));

        model.addAttribute(SORT_ATTR, sort);

        return "user/admin/all";
    }

    @GetMapping("/profile")
    public String showCurrentUserProfile(Authentication authentication, Model model,
                                         @RequestParam(defaultValue = DEFAULT_REVIEWS_PAGE_NUMBER, name = REVIEW_PAGE_NUMBER_ATTR) Integer reviewPageNumber,
                                         @RequestParam(defaultValue = DEFAULT_REVIEWS_SORT, name = REVIEW_SORT_ATTR) String reviewSort) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }
        User currentUser = userService.getByUsername(authentication.getName());

        model.addAttribute(USER_ATTR, currentUser);

        if (!model.containsAttribute(IMAGE_DTO_ATTR)) {
            model.addAttribute(IMAGE_DTO_ATTR, new ImageDto());
        }

        model.addAttribute(REVIEW_PAGE_ATTR, reviewService.getReviewsByUserId(currentUser.getId(), PageRequest.of(reviewPageNumber, REVIEW_PAGE_SIZE, sortUtility.parseSort(reviewSort, Review.class))));
        model.addAttribute(REVIEW_SORT_ATTR, reviewSort);

        return "user/profile";
    }

    @GetMapping("/admin/new")
    public String newUser(Model model) {

        if (!model.containsAttribute(USER_ATTR)) {
            model.addAttribute(USER_ATTR, new User());
        }
        model.addAttribute(ROLE_LIST_ATTR, Role.values());

        return "user/admin/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute(USER_ATTR) @Valid User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        uniquenessValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(USER_ATTR, user);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + USER_ATTR, bindingResult);
            return "redirect:/users/admin/new";
        }
        userService.create(user);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @PatchMapping ("/admin/is-enabled/{id}")
    public String toggleStatus(@PathVariable(ID) Long id) {

        userService.toggleStatus(id);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }



    @GetMapping("/profile/password/edit")
    public String editCurrentUserPassword(Model model) {

        model.addAttribute(PASSWORD_UPDATE_DTO_ATTR, new PasswordUpdateDto());
        return PASSWORD_EDIT_PAGE;
    }

    @PatchMapping("/profile/password")
    public String updateCurrentUserPassword(@ModelAttribute(PASSWORD_UPDATE_DTO_ATTR) @Valid PasswordUpdateDto passwordUpdateDto, BindingResult bindingResult, Authentication authentication) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        if(bindingResult.hasErrors()) {
            return PASSWORD_EDIT_PAGE;
        }

        String username = authentication.getName();
        if (!userService.isCorrectCredentials(username, passwordUpdateDto.getOldPassword())) {
            bindingResult.rejectValue(OLD_PASSWORD_FIELD_ATTR, PASSWORD_UPDATE_DTO_ATTR + "." + OLD_PASSWORD_FIELD_ATTR, "Wrong old password.");
            return PASSWORD_EDIT_PAGE;
        }

        userService.updatePassword(userService.getByUsername(username).getId(), passwordUpdateDto.getNewPassword());

        return "redirect:/users/profile";
    }


    @GetMapping("/profile/public-name/edit")
    public String editCurrentUserPublicName(Model model, Authentication authentication) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }
        User currentUser = userService.getByUsername(authentication.getName());

        model.addAttribute(PUBLIC_NAME_UPDATE_DTO_ATTR, new PublicNameUpdateDto(currentUser.getPublicName()));
        return "user/public-name-edit";
    }

    @PatchMapping("/profile/public-name")
    public String updateCurrentUserPublicName(@ModelAttribute(PUBLIC_NAME_UPDATE_DTO_ATTR) @Valid PublicNameUpdateDto publicNameUpdateDto, BindingResult bindingResult, Authentication authentication) {

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

    @GetMapping("/admin/role/edit/{id}")
    public String editRole(@PathVariable(ID) Long id, Model model) {

        User user = userService.getById(id);

        if(!model.containsAttribute(ROLE_UPDATE_DTO_ATTR)) {
            model.addAttribute(ROLE_UPDATE_DTO_ATTR, new RoleUpdateDto(user.getId(), user.getRole()));
        }
        model.addAttribute(ROLE_LIST_ATTR, Role.values());

        return "user/admin/role-edit";
    }

    @PatchMapping("/admin/role")
    public String updateRole(@ModelAttribute(ROLE_UPDATE_DTO_ATTR) @Valid RoleUpdateDto roleUpdateDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute(ID, roleUpdateDto.getId());
            redirectAttributes.addFlashAttribute(ROLE_UPDATE_DTO_ATTR, roleUpdateDto);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + ROLE_UPDATE_DTO_ATTR, bindingResult);
            return "redirect:/users/admin/role/edit/{id}";
        }

        userService.updateRole(roleUpdateDto.getId(), roleUpdateDto.getRole());

        return "redirect:/users/admin";
    }

    @PostMapping("/profile/upload-image")
    public String uploadImage(Authentication authentication, @ModelAttribute(IMAGE_DTO_ATTR) @Valid ImageDto imageDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }
        User currentUser = userService.getByUsername(authentication.getName());

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(IMAGE_DTO_ATTR, imageDto);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + IMAGE_DTO_ATTR, bindingResult);
            return "redirect:/users/profile";
        }

        userService.updateImg(currentUser.getId(), imageDto.getImage());

        return "redirect:/users/profile";
    }

    @DeleteMapping("/profile/review/{id}")
    public String deleteCurrentUserReview(@PathVariable(ID) Long id, Authentication authentication) {

        User currentUser = userService.getByUsername(authentication.getName());

        reviewService.deleteReviewIfOwnedByUser(id, currentUser.getId());

        return "redirect:/users/profile";
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable(ID) Long id) {

        userService.delete(id);
        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

}
