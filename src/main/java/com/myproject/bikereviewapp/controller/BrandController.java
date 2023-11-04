package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/admin/new")
    public String newBrand(@ModelAttribute Brand brand) {
        return "brand/admin/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute @Valid Brand brand, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return newBrand(brand);
        }
        brandService.create(brand);

        return "redirect:/brands/admin";
    }
}
