package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Role;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import com.myproject.bikereviewapp.validation.validator.UserUniquenessValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    UserUniquenessValidator uniquenessValidator;

    @Autowired
    MockMvc mockMvc;


    private static User validInputUser;
    private static User invalidInputUser;

    @BeforeAll
    static void init() {
        validInputUser = new User(null, "someUsername", "somePassword", false, null, "somePublicName");
        invalidInputUser = new User(null, "", "", false, null, "somePublicName");
    }

    @Test
    void signUp_shouldRedirectToLogInPage_ifUserIsValid() throws Exception {

        mockMvc.perform(post("/signup")
                        .param("username", validInputUser.getUsername())
                        .param("password", validInputUser.getPassword())
                        .param("publicName", validInputUser.getPublicName())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void signUp_shouldCreateEnabledUserWithClientRole_ifUserIsValid() throws Exception {

        User expectedUser = new User(null, validInputUser.getUsername(), validInputUser.getPassword(), true, Role.CLIENT, validInputUser.getPublicName());

        mockMvc.perform(post("/signup")
                .param("username", validInputUser.getUsername())
                .param("password", validInputUser.getPassword())
                .param("publicName", validInputUser.getPublicName())
        );

        verify(userService).create(expectedUser);
    }

    @Test
    void signUp_shouldCheckUserForUniqueness_ifUserIsValid() throws Exception {

        mockMvc.perform(post("/signup")
                .param("username", validInputUser.getUsername())
                .param("password", validInputUser.getPassword())
                .param("publicName", validInputUser.getPublicName())
        );

        verify(uniquenessValidator).validate(any(User.class), any(BindingResult.class));
    }

    @Test
    void signUp_shouldReturnSignUpForm_ifUserIsInvalid() throws Exception {

        mockMvc.perform(post("/signup")
                        .param("username", invalidInputUser.getUsername())
                        .param("password", invalidInputUser.getPassword())
                        .param("publicName", invalidInputUser.getPublicName())
                ).andExpect(status().isOk())
                .andExpect(view().name("auth/signup"));
    }

    @Test
    void signUp_shouldReturnFieldErrors_ifUserIsInvalid() throws Exception {

        mockMvc.perform(post("/signup")
                        .param("username", invalidInputUser.getUsername())
                        .param("password", invalidInputUser.getPassword())
                        .param("publicName", invalidInputUser.getPublicName())
                ).andExpect(model().attributeHasErrors("user"));
    }

    @Test
    void signUp_shouldNeverCreateUser_ifUserIsInvalid() throws Exception {

        mockMvc.perform(post("/signup")
                .param("username", invalidInputUser.getUsername())
                .param("password", invalidInputUser.getPassword())
                .param("publicName", invalidInputUser.getPublicName())
        );

        verify(userService, never()).create(any(User.class));
    }
}