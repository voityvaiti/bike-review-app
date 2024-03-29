package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.utility.SortUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private static final String REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL = "redirect:/brands/admin";


    private static final String BRAND_ATTR = "brand";
    private static final String BRAND_PAGE_ATTR = "brandPage";


    private final BrandService brandService;

    private final SortUtility sortUtility;



    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model,
                                      @RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "20") Integer pageSize,
                                      @RequestParam(defaultValue = "id:asc") String sort) {

        model.addAttribute(BRAND_PAGE_ATTR, brandService.getAll(PageRequest.of(pageNumber, pageSize, sortUtility.parseSort(sort))));

        model.addAttribute("currentPageNumber", pageNumber);
        model.addAttribute("currentSort", sort);

        return "brand/admin/all";
    }

    @GetMapping("/admin/new")
    public String newBrand(@ModelAttribute Brand brand) {
        return "brand/admin/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute @Valid Brand brand, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return newBrand(brand);
        }
        brandService.create(brand);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }


    @GetMapping("/admin/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        if (!model.containsAttribute(BRAND_ATTR)) {
            model.addAttribute(BRAND_ATTR, brandService.getById(id));
        }
        return "brand/admin/edit";
    }

    @PutMapping("/admin/{id}")
    public String update(@PathVariable Long id, @ModelAttribute @Valid Brand brand, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return edit(id, model);
        }
        brandService.update(id, brand);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable Long id) {

        brandService.delete(id);
        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }
}
