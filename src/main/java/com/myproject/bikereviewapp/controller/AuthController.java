package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import com.myproject.bikereviewapp.validation.validator.UserUniquenessValidator;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthController {

    private final UserService userService;

    private final UserUniquenessValidator uniquenessValidator;

    public AuthController(UserService userService, UserUniquenessValidator uniquenessValidator) {
        this.userService = userService;
        this.uniquenessValidator = uniquenessValidator;
    }

    @GetMapping("/login")
    public String showLogInForm() {
        return "auth/login";
    }

    @GetMapping("/login-error")
    public String showLogInErrorForm(Model model) {

        model.addAttribute("error", true);
        return "auth/login";
    }

    @GetMapping("/logout")
    public String showLogOutPage() {
        return "auth/logout";
    }


    @GetMapping("/signup")
    public String showSignUpForm(@ModelAttribute("user") User user) {

        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {

        uniquenessValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()) {
            return showSignUpForm(user);
        }
        user.setRole(Role.CLIENT);
        user.setEnabled(true);

        userService.create(user);

        return "redirect:/login";
    }

}
