package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.exceptionhandler.exception.UserIsNotAuthorizedException;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final String USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE = "Error! User is not authorized.";

    private static final String REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL = "redirect:/users/admin";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model) {
        model.addAttribute("users", userService.getAll());
        return "user/admin/all";
    }

    @GetMapping("/profile")
    public String showCurrentUserProfile(Authentication authentication, Model model) {

        if (authentication == null) {
            throw new UserIsNotAuthorizedException(USER_IS_NOT_AUTHORIZED_ERROR_MESSAGE);
        }
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("user", userService.getByUsername(userDetails.getUsername()));

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

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable Long id) {

        userService.delete(id);
        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

}
