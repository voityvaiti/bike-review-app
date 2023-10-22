package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLogInPage() {
        return "auth/login";
    }


    @GetMapping("/signup")
    public String getSignUpPage(Model model) {

        model.addAttribute("user", new User());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute("user") User user, Model model) {

        if(userService.exists(user.getUsername())) {

            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "User with same username is already exists");
            return "auth/signup";
        }
        user.setRole(Role.CLIENT);
        user.setEnabled(true);

        userService.create(user);

        return "redirect:/login";
    }

}
