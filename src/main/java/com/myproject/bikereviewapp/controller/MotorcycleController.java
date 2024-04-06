package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import com.myproject.bikereviewapp.utility.SortUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.myproject.bikereviewapp.controller.RedirectController.BINDING_RESULT_ATTR;
import static com.myproject.bikereviewapp.controller.ReviewController.*;

@Controller
@RequestMapping("/motorcycles")
@RequiredArgsConstructor
public class MotorcycleController {

    private static final String REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL = "redirect:/motorcycles/admin";

    public static final Integer MOTORCYCLE_MAIN_PAGE_SIZE = 16;

    private static final String MOTORCYCLE_ATTR = "motorcycle";
    private static final String MOTORCYCLE_PAGE_ATTR = "motorcyclePage";
    private static final String BRANDS_ATTR = "brands";


    private final MotorcycleService motorcycleService;
    private final BrandService brandService;
    private final ReviewService reviewService;
    private final UserService userService;

    private final SortUtility sortUtility;



    @GetMapping
    public String showAll(Model model,
                          @RequestParam(defaultValue = "0") Integer pageNumber,
                          @RequestParam(defaultValue = "reviewsAmount:desc") String sort,
                          @RequestParam(required = false, name = "q") String query) {

        Page<Motorcycle> motorcyclePage;
        Pageable pageable = PageRequest.of(pageNumber, MOTORCYCLE_MAIN_PAGE_SIZE, sortUtility.parseSort(sort));

        if (query != null && !query.isBlank()) {
            motorcyclePage = motorcycleService.getAllByQuery(query, pageable);
        } else {
            motorcyclePage = motorcycleService.getAll(pageable);
        }

        model.addAttribute(MOTORCYCLE_PAGE_ATTR, motorcyclePage);

        model.addAttribute("currentPageNumber", pageNumber);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentQuery", query);

        return "motorcycle/all";
    }

    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model,
                                      @RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "20") Integer pageSize,
                                      @RequestParam(defaultValue = "id:asc") String sort) {

        model.addAttribute(MOTORCYCLE_PAGE_ATTR, motorcycleService.getAll(PageRequest.of(pageNumber, pageSize, sortUtility.parseSort(sort))));

        model.addAttribute("currentPageNumber", pageNumber);
        model.addAttribute("currentSort", sort);

        return "motorcycle/admin/all";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long motorcycleId,
                       @RequestParam(defaultValue = DEFAULT_REVIEWS_PAGE_NUMBER, name = REVIEW_PAGE_NUMBER_ATTR) Integer reviewPageNumber,
                       @RequestParam(defaultValue = DEFAULT_REVIEWS_SORT, name = REVIEW_SORT_ATTR) String reviewSort,
                       Model model, Authentication authentication) {

        model.addAttribute(MOTORCYCLE_ATTR, motorcycleService.getById(motorcycleId));


        Review currentUserReview = null;

        if(authentication != null) {
            User currentUser = userService.getByUsername(authentication.getName());

            currentUserReview = reviewService.getIfExistsUserReviewOnMotorcycle(currentUser.getId(), motorcycleId);
        }

        if(!model.containsAttribute(NEW_REVIEW_ATTR)) {
            model.addAttribute(NEW_REVIEW_ATTR, new Review());
        }
        model.addAttribute(REVIEW_PAGE_ATTR, reviewService.getReviewsByMotorcycleId(motorcycleId, PageRequest.of(reviewPageNumber, REVIEWS_PAGE_SIZE, sortUtility.parseSort(reviewSort))));
        model.addAttribute("currentUserReview", currentUserReview);

        model.addAttribute("currentReviewPageNumber", reviewPageNumber);
        model.addAttribute("currentReviewSort", reviewSort);


        return "motorcycle/show";
    }

    @GetMapping("/admin/new")
    public String newMotorcycle(Model model) {

        if (!model.containsAttribute(MOTORCYCLE_ATTR)) {
            model.addAttribute(MOTORCYCLE_ATTR, new Motorcycle());
        }
        model.addAttribute(BRANDS_ATTR, brandService.getAll());

        return "motorcycle/admin/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute @Valid Motorcycle motorcycle, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(MOTORCYCLE_ATTR, motorcycle);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + MOTORCYCLE_ATTR, bindingResult);
            return "redirect:/motorcycles/admin/new";
        }
        motorcycleService.create(motorcycle);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @GetMapping("/admin/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        if (!model.containsAttribute(MOTORCYCLE_ATTR)) {
            model.addAttribute(MOTORCYCLE_ATTR, motorcycleService.getById(id));
        }
        model.addAttribute(BRANDS_ATTR, brandService.getAll());

        return "motorcycle/admin/edit";
    }

    @PutMapping("/admin/{id}")
    public String update(@PathVariable Long id, @ModelAttribute @Valid Motorcycle motorcycle, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute("id", id);
            redirectAttributes.addFlashAttribute(MOTORCYCLE_ATTR, motorcycle);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + MOTORCYCLE_ATTR, bindingResult);

            return "redirect:/motorcycles/admin/edit/{id}";
        }
        motorcycleService.update(id, motorcycle);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable Long id) {

        motorcycleService.delete(id);
        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }
}
