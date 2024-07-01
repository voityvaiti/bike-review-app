package com.myproject.bikereviewapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    protected static final String ID = "id";

    protected static final String PAGE_NUMBER_ATTR = "pageNumber";
    protected static final String PAGE_SIZE_ATTR = "pageSize";
    protected static final String SORT_ATTR = "sort";
    protected static final String IMAGE_DTO_ATTR = "imageDto";

    protected static final String DEFAULT_PAGE_NUMBER = "0";
    protected static final String DEFAULT_ADMIN_PAGE_SIZE = "20";
    protected static final String DEFAULT_ADMIN_PAGE_SORT = "id:asc";

    protected static final String BINDING_RESULT_ATTR = "org.springframework.validation.BindingResult.";



    @GetMapping("/")
    public String redirectToMainPage() {
        return "redirect:/motorcycles";
    }

    @GetMapping("/admin")
    public String redirectToMainAdminPanelPage() {
        return "redirect:/motorcycles/admin";
    }

}
