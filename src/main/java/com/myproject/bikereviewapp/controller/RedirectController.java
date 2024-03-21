package com.myproject.bikereviewapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {


    public static final String BINDING_RESULT_ATTR = "org.springframework.validation.BindingResult.";

    @GetMapping("/")
    public String redirectToMainPage() {
        return "redirect:/motorcycles";
    }

    @GetMapping("/admin")
    public String redirectToMainAdminPanelPage() {
        return "redirect:/motorcycles/admin";
    }

}
