package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import com.myproject.bikereviewapp.validation.validator.UserUniquenessValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.myproject.bikereviewapp.controller.RedirectController.BINDING_RESULT_ATTR;
import static com.myproject.bikereviewapp.controller.UserController.USER_ATTR;


@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final UserUniquenessValidator uniquenessValidator;



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
    public String showSignUpForm(Model model) {

        if (!model.containsAttribute(USER_ATTR)) {
            model.addAttribute(USER_ATTR, new User());
        }
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        uniquenessValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(USER_ATTR, user);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + USER_ATTR, bindingResult);
            return "redirect:/signup";
        }
        user.setRole(Role.CLIENT);
        user.setEnabled(true);

        userService.create(user);

        return "redirect:/login";
    }

}
