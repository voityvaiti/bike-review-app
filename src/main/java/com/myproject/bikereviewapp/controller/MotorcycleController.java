package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/motorcycles")
public class MotorcycleController {

    private final MotorcycleService motorcycleService;

    private final BrandService brandService;

    private final ReviewService reviewService;

    public MotorcycleController(MotorcycleService motorcycleService, BrandService brandService, ReviewService reviewService) {
        this.motorcycleService = motorcycleService;
        this.brandService = brandService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String showAll(Model model) {

        model.addAttribute("motorcycles", motorcycleService.getAll());
        return "motorcycle/all";
    }

    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model) {
        model.addAttribute("motorcycles", motorcycleService.getAll());
        return "motorcycle/admin/all";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id,
                       @ModelAttribute("newReview") Review newReview, Model model) {

        model.addAttribute("motorcycle", motorcycleService.getById(id));
        model.addAttribute("reviews", reviewService.getReviewsByMotorcycleId(id));

        return "motorcycle/show";
    }

    @GetMapping("/admin/new")
    public String getCreateMotorcyclePage(@ModelAttribute Motorcycle motorcycle, Model model) {

        model.addAttribute("brands", brandService.getAll());
        return "motorcycle/admin/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute @Valid Motorcycle motorcycle, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()) {
            return getCreateMotorcyclePage(motorcycle, model);
        }
        motorcycleService.create(motorcycle);

        return "redirect:/motorcycles/admin";
    }
}
