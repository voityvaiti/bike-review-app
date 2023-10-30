package com.myproject.bikereviewapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/")
    public String redirectToMainPage() {
        return "redirect:/motorcycles";
    }

    @GetMapping("/admin")
    public String redirectToMainAdminPanelPage() {
        return "redirect:/motorcycles/admin";
    }

}
