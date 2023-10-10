package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/motorcycles")
public class MotorcycleController {

    private final MotorcycleService motorcycleService;

    public MotorcycleController(MotorcycleService motorcycleService) {
        this.motorcycleService = motorcycleService;
    }

    @GetMapping
    public String index(Model model) {

        model.addAttribute("motorcycles", motorcycleService.getAll());
        return "motorcycle/all";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {

        model.addAttribute("motorcycle", motorcycleService.getById(id));
        model.addAttribute("review", new Review());

        return "motorcycle/show";
    }
}
