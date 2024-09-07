package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public abstract class BaseController {

    private static final String USER_IMG_URL_ATTR = "userImgUrl";

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addUserImageUrlToModel(Authentication authentication, Model model) {

        if (authentication != null && authentication.isAuthenticated()) {

            User currentUser = userService.getByUsername(authentication.getName());

            if(currentUser.getImage() != null) {
                model.addAttribute(USER_IMG_URL_ATTR, currentUser.getImage().getUrl());
            }
        }
    }

}
