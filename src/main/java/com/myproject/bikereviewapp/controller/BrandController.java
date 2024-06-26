package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.service.abstraction.CloudService;
import com.myproject.bikereviewapp.utility.SortUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.myproject.bikereviewapp.controller.MainController.*;

@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {


    protected static final String BRAND_ATTR = "brand";
    protected static final String BRAND_PAGE_ATTR = "brandPage";

    private static final String REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL = "redirect:/brands/admin";

    private final BrandService brandService;

    private final SortUtility sortUtility;


    @GetMapping("/admin")
    public String showAllInAdminPanel(Model model,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER, name = PAGE_NUMBER_ATTR) Integer pageNumber,
                                      @RequestParam(defaultValue = DEFAULT_ADMIN_PAGE_SIZE, name = PAGE_SIZE_ATTR) Integer pageSize,
                                      @RequestParam(defaultValue = DEFAULT_ADMIN_PAGE_SORT, name = SORT_ATTR) String sort) {

        model.addAttribute(BRAND_PAGE_ATTR, brandService.getAll(PageRequest.of(pageNumber, pageSize, sortUtility.parseSort(sort))));

        model.addAttribute(SORT_ATTR, sort);

        return "brand/admin/all";
    }

    @GetMapping("/admin/new")
    public String newBrand(Model model) {

        if (!model.containsAttribute(BRAND_ATTR)) {
            model.addAttribute(BRAND_ATTR, new Brand());
        }
        return "brand/admin/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute(BRAND_ATTR) @Valid Brand brand, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(BRAND_ATTR, brand);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + BRAND_ATTR, bindingResult);
            return "redirect:/brands/admin/new";
        }

        brandService.create(brand);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }


    @GetMapping("/admin/edit/{id}")
    public String edit(@PathVariable(ID) Long id, Model model) {

        if (!model.containsAttribute(BRAND_ATTR)) {
            model.addAttribute(BRAND_ATTR, brandService.getById(id));
        }
        return "brand/admin/edit";
    }

    @PutMapping("/admin/{id}")
    public String update(@PathVariable(ID) Long id, @ModelAttribute(BRAND_ATTR) @Valid Brand brand, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            redirectAttributes.addAttribute(ID, id);
            redirectAttributes.addFlashAttribute(BRAND_ATTR, brand);
            redirectAttributes.addFlashAttribute(BINDING_RESULT_ATTR + BRAND_ATTR, bindingResult);
            return "redirect:/brands/admin/edit/{id}";
        }

        brandService.update(id, brand);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @PostMapping("/admin/{id}/upload-image")
    public String uploadImage(@PathVariable("id") Long id, @RequestParam("image") MultipartFile image) {

        brandService.uploadImg(id, image);

        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable(ID) Long id) {

        brandService.delete(id);
        return REDIRECT_TO_SHOW_ALL_IN_ADMIN_PANEL;
    }
}
