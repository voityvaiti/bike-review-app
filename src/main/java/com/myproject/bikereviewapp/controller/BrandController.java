package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.service.abstraction.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model) {
        model.addAttribute("brands", brandService.getAll());
        return "brand/admin/all";
    }
}
