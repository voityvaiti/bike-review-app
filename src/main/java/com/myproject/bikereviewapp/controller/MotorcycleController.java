package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.entity.dto.ImageDto;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.myproject.bikereviewapp.controller.MainController.*;
import static com.myproject.bikereviewapp.controller.ReviewController.*;

@Controller
@RequestMapping("/motorcycles")
@RequiredArgsConstructor
public class MotorcycleController {

    protected static final Integer MOTORCYCLE_MAIN_PAGE_SIZE = 16;
    protected static final String MOTORCYCLE_MAIN_PAGE_DEFAULT_SORT = "reviewsAmount:desc";


    protected static final String MOTORCYCLE_ATTR = "motorcycle";
    protected static final String MOTORCYCLE_PAGE_ATTR = "motorcyclePage";
    protected static final String BRAND_LIST_ATTR = "brands";


    private static final String REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL = "redirect:/motorcycles/admin";

    private final MotorcycleService motorcycleService;
    private final BrandService brandService;
    private final ReviewService reviewService;
    private final UserService userService;

    private final SortUtility sortUtility;



    @GetMapping
    public String showAll(Model model,
                          @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER, name = PAGE_NUMBER_ATTR) Integer pageNumber,
                          @RequestParam(defaultValue = MOTORCYCLE_MAIN_PAGE_DEFAULT_SORT, name = SORT_ATTR) String sort,
                          @RequestParam(required = false, name = "query") String query) {

        Page<Motorcycle> motorcyclePage;
        Pageable pageable = PageRequest.of(pageNumber, MOTORCYCLE_MAIN_PAGE_SIZE, sortUtility.parseSort(sort));

        if (query != null && !query.isBlank()) {
            motorcyclePage = motorcycleService.getAllByQuery(query, pageable);
        } else {
            motorcyclePage = motorcycleService.getAll(pageable);
        }

        model.addAttribute(MOTORCYCLE_PAGE_ATTR, motorcyclePage);

        model.addAttribute(SORT_ATTR, sort);
        model.addAttribute("query", query);

        return "motorcycle/all";
    }

    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER, name = PAGE_NUMBER_ATTR) Integer pageNumber,
                                      @RequestParam(defaultValue = DEFAULT_ADMIN_PAGE_SIZE, name = PAGE_SIZE_ATTR) Integer pageSize,
                                      @RequestParam(defaultValue = DEFAULT_ADMIN_PAGE_SORT, name = SORT_ATTR) String sort) {

        model.addAttribute(MOTORCYCLE_PAGE_ATTR, motorcycleService.getAll(PageRequest.of(pageNumber, pageSize, sortUtility.parseSort(sort))));

        model.addAttribute(SORT_ATTR, sort);

        return "motorcycle/admin/all";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable(ID) Long motorcycleId,
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
        model.addAttribute(REVIEW_PAGE_ATTR, reviewService.getReviewsByMotorcycleId(motorcycleId, PageRequest.of(reviewPageNumber, REVIEW_PAGE_SIZE, sortUtility.parseSort(reviewSort))));
        model.addAttribute("currentUserReview", currentUserReview);

        model.addAttribute(REVIEW_SORT_ATTR, reviewSort);


        return "motorcycle/show";
    }

    @GetMapping("/admin/new")
    public String newMotorcycle(Model model) {

        if (!model.containsAttribute(MOTORCYCLE_ATTR)) {
            model.addAttribute(MOTORCYCLE_ATTR, new Motorcycle());
        }
        model.addAttribute(BRAND_LIST_ATTR, brandService.getAll());

        return "motorcycle/admin/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute(MOTORCYCLE_ATTR) @Valid Motorcycle motorcycle, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(MOTORCYCLE_ATTR, motorcycle);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + MOTORCYCLE_ATTR, bindingResult);
            return "redirect:/motorcycles/admin/new";
        }
        motorcycleService.create(motorcycle);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @GetMapping("/admin/edit/{id}")
    public String edit(@PathVariable(ID) Long id, Model model) {

        if (!model.containsAttribute(MOTORCYCLE_ATTR)) {
            model.addAttribute(MOTORCYCLE_ATTR, motorcycleService.getById(id));
        }
        model.addAttribute(BRAND_LIST_ATTR, brandService.getAll());

        if (!model.containsAttribute(IMAGE_DTO_ATTR)) {
            model.addAttribute(IMAGE_DTO_ATTR, new ImageDto());
        }

        return "motorcycle/admin/edit";
    }

    @PutMapping("/admin/{id}")
    public String update(@PathVariable(ID) Long id, @ModelAttribute(MOTORCYCLE_ATTR) @Valid Motorcycle motorcycle, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute(ID, id);
            redirectAttributes.addFlashAttribute(MOTORCYCLE_ATTR, motorcycle);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + MOTORCYCLE_ATTR, bindingResult);

            return "redirect:/motorcycles/admin/edit/{id}";
        }
        motorcycleService.update(id, motorcycle);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @PostMapping("/admin/{id}/upload-image")
    public String uploadImage(@PathVariable("id") Long id, @ModelAttribute(IMAGE_DTO_ATTR) @Valid ImageDto imageDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute(ID, id);
            redirectAttributes.addFlashAttribute(IMAGE_DTO_ATTR, imageDto);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + IMAGE_DTO_ATTR, bindingResult);
            return "redirect:/motorcycles/admin/edit/{id}";
        }

        motorcycleService.updateImg(id, imageDto.getImage());

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }


    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable(ID) Long id) {

        motorcycleService.delete(id);
        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }
}
