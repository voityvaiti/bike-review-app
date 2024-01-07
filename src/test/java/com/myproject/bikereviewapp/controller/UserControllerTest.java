
package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.*;
import com.myproject.bikereviewapp.entity.dto.PublicNameUpdateDto;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.service.abstraction.UserService;
import com.myproject.bikereviewapp.utility.SortUtility;
import com.myproject.bikereviewapp.validation.validator.UserUniquenessValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private static final String SAMPLE_VIEW = "some/view";

    @MockBean
    UserService userService;

    @MockBean
    ReviewService reviewService;

    @MockBean
    UserUniquenessValidator uniquenessValidator;

    @MockBean
    SortUtility sortUtility;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserController userController;


    private static Page<User> userPage;
    private static Page<Review> reviewPage;
    private static User user;
    private static final String USERNAME = "someUsername";
    private static PublicNameUpdateDto publicNameUpdateDto;
    private static int pageNumber;
    private static int pageSize;
    private static Sort sort;
    private static String sortStr;
    private static BindingResult mockBindingResult;
    private static Model mockModel;
    private static Authentication mockAuthentication;


    @BeforeAll
    static void init() {
        userPage = new PageImpl<>(List.of(
                new User(1L, "username1", "password1", true, Role.CLIENT, "publicName1"),
                new User(2L, "username2", "password2", true, Role.CLIENT, "publicName2"),
                new User(3L, "username3", "password3", true, Role.CLIENT, "publicName3")
        ));

        Motorcycle sampleMotorcycle = new Motorcycle(1L, "someModel", new Brand(1L, "someName", "someCountry"));
        reviewPage = new PageImpl<>(List.of(
                new Review(1L, "bodyContent", LocalDate.of(2000, 1, 10), (short) 3, sampleMotorcycle, user),
                new Review(2L, "bodyContent", LocalDate.of(2000, 2, 20), (short) 4, sampleMotorcycle, user)
        ));

        user = new User(8L, USERNAME, "somePassword", true, Role.CLIENT, "somePublicName");
        publicNameUpdateDto = new PublicNameUpdateDto(user.getPublicName());

        pageNumber = 5;
        pageSize = 12;
        sort = Sort.by(Sort.Direction.ASC, "id");
        sortStr = "id:asc";
        mockBindingResult = mock(BindingResult.class);
        mockModel = mock(Model.class);

        mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(USERNAME);
    }

    @BeforeEach
    void setUp() {
        when(userService.getAll(any(PageRequest.class))).thenReturn(userPage);
        when(userService.getByUsername(anyString())).thenReturn(user);
        when(reviewService.getReviewsByUserId(anyLong(), any(Pageable.class))).thenReturn(reviewPage);

        when(sortUtility.parseSort(anyString())).thenReturn(sort);
        when(mockBindingResult.hasErrors()).thenReturn(false);
    }


    @Test
    void showAllInAdminPanel_shouldReturnAppropriateView() throws Exception {

        mockMvc.perform(get("/users/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/admin/all"));
    }

    @Test
    void showAllInAdminPanel_shouldAddUserPageModelAttribute() throws Exception {

        mockMvc.perform(get("/users/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userPage", userPage));
    }

    @Test
    void showAllInAdminPanel_shouldMakePageRequestByProperParams_whenRequestContainAppropriateParams() throws Exception {

        when(sortUtility.parseSort(sortStr)).thenReturn(sort);

        mockMvc.perform(get("/users/admin")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sortStr))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(userService).getAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    @Test
    void showAllInAdminPanel_shouldAddPageNumberAndSortModelAttributes_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/users/admin")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sortStr))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPageNumber", pageNumber))
                .andExpect(model().attribute("currentSort", sortStr));
    }

    @Test
    void showAllInAdminPanel_shouldAddDefaultPageNumberAndSortModelAttributes_whenRequestDoesNotContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/users/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentPageNumber"))
                .andExpect(model().attributeExists("currentSort"));
    }

    @Test
    void showCurrentUserProfile_shouldReturnAppropriateView() throws Exception {

        mockMvc.perform(get("/users/profile")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"));
    }

    @Test
    void showCurrentUserProfile_shouldAddUserModelAttribute() throws Exception {

        mockMvc.perform(get("/users/profile")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user));
    }

    @Test
    void showCurrentUserProfile_shouldAddReviewPageModelAttribute() throws Exception {

        mockMvc.perform(get("/users/profile")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute("reviewPage", reviewPage));
    }

    @Test
    void showCurrentUserProfile_shouldShouldGetUserByProperUsername() throws Exception {

        when(userService.getByUsername(USERNAME)).thenReturn(user);

        mockMvc.perform(get("/users/profile")
                        .principal(mockAuthentication))
                .andExpect(status().isOk());

        verify(userService).getByUsername(USERNAME);
    }

    @Test
    void showCurrentUserProfile_shouldMakeReviewPageRequestByProperParams_whenRequestContainAppropriateParams() throws Exception {

        when(sortUtility.parseSort(sortStr)).thenReturn(sort);

        mockMvc.perform(get("/users/profile")
                        .param("reviewPageNumber", String.valueOf(pageNumber))
                        .param("reviewPageSize", String.valueOf(pageSize))
                        .param("reviewSort", sortStr)
                        .principal(mockAuthentication))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(reviewService).getReviewsByUserId(user.getId(), PageRequest.of(pageNumber, pageSize, sort));
    }

    @Test
    void showCurrentUserProfile_shouldAddReviewPageNumberAndSortModelAttributes_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/users/profile")
                        .param("reviewPageNumber", String.valueOf(pageNumber))
                        .param("reviewPageSize", String.valueOf(pageSize))
                        .param("reviewSort", sortStr)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentReviewPageNumber", pageNumber))
                .andExpect(model().attribute("currentReviewSort", sortStr));
    }

    @Test
    void showCurrentUserProfile_shouldAddDefaultReviewPageNumberAndSortModelAttributes_whenRequestDoesNotContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/users/profile")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentReviewPageNumber"))
                .andExpect(model().attributeExists("currentReviewSort"));
    }

    @Test
    void showCurrentUserProfile_shouldForbidRequest_whenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getByUsername(anyString());
        verify(reviewService, never()).getReviewsByUserId(anyLong(), any(PageRequest.class));
    }

    @Test
    void create_shouldCreateUser_ifUserIsValid() {

        userController.create(user, mockBindingResult, mockModel);

        verify(userService).create(user);
    }

    @Test
    void create_shouldNeverCreateUser_ifUserIsInvalid() {

        UserController spyUserController = spy(userController);
        doReturn(SAMPLE_VIEW).when(spyUserController).newUser(any(User.class), any(Model.class));

        when(mockBindingResult.hasErrors()).thenReturn(true);

        spyUserController.create(user, mockBindingResult, mockModel);

        verify(userService, never()).create(any(User.class));
    }

    @Test
    void editCurrentUserPublicName_shouldReturnAppropriateView() throws Exception {

        mockMvc.perform(get("/users/profile/public-name-edit")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(view().name("user/public-name-edit"));
    }

    @Test
    void editCurrentUserPublicName_shouldAddProperDtoModelAttribute() throws Exception {

        mockMvc.perform(get("/users/profile/public-name-edit")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute("publicNameUpdateDto", publicNameUpdateDto));
    }

    @Test
    void editCurrentUserPublicName_shouldShouldGetUserByProperUsername() throws Exception {

        when(userService.getByUsername(USERNAME)).thenReturn(user);

        mockMvc.perform(get("/users/profile/public-name-edit")
                        .principal(mockAuthentication))
                .andExpect(status().isOk());

        verify(userService).getByUsername(USERNAME);
    }

    @Test
    void editCurrentUserPublicName_shouldForbidRequest_whenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(get("/users/profile/public-name-edit"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getByUsername(anyString());
    }

}