package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/motorcycles")
public class MotorcycleController {

    private final MotorcycleService motorcycleService;

    private final ReviewService reviewService;

    public MotorcycleController(MotorcycleService motorcycleService, ReviewService reviewService) {
        this.motorcycleService = motorcycleService;
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
}
