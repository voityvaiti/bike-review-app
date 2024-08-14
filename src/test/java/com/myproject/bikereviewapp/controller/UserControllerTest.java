
package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.*;
import com.myproject.bikereviewapp.entity.dto.PasswordUpdateDto;
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

import java.time.LocalDate;
import java.util.List;

import static com.myproject.bikereviewapp.controller.MainController.*;
import static com.myproject.bikereviewapp.controller.ReviewController.*;
import static com.myproject.bikereviewapp.controller.UserController.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

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


    private static Page<User> userPage;
    private static Page<Review> reviewPage;
    private static User user;
    private static final String USERNAME = "someUsername";
    private static PasswordUpdateDto passwordUpdateDto;
    private static PublicNameUpdateDto publicNameUpdateDto;
    private static int pageNumber;
    private static int pageSize;
    private static Sort sort;
    private static String sortStr;
    private static Authentication mockAuthentication;


    @BeforeAll
    static void init() {
        userPage = new PageImpl<>(List.of(
                new User(),
                new User(),
                new User()
        ));

        Motorcycle sampleMotorcycle = new Motorcycle();
        sampleMotorcycle.setId(1L);
        sampleMotorcycle.setModel("someModel");
        sampleMotorcycle.setBrand(new Brand());

        reviewPage = new PageImpl<>(List.of(
                new Review(1L, "bodyContent", LocalDate.of(2000, 1, 10), (short) 3, sampleMotorcycle, user, 10, 5),
                new Review(2L, "bodyContent", LocalDate.of(2000, 2, 20), (short) 4, sampleMotorcycle, user, 25, 1)
        ));

        user = new User();
        user.setId(8L);
        user.setUsername(USERNAME);
        user.setPassword("somePassword");
        user.setEnabled(true);
        user.setRole(Role.CLIENT);
        user.setPublicName("somePublicName");


        passwordUpdateDto = new PasswordUpdateDto("oldPassword", "newPassword");
        publicNameUpdateDto = new PublicNameUpdateDto(user.getPublicName());


        pageNumber = 5;
        pageSize = 12;
        sort = Sort.by(Sort.Direction.ASC, "id");
        sortStr = "id:asc";

        mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(USERNAME);
    }

    @BeforeEach
    void setUp() {
        when(userService.getAll(any(PageRequest.class))).thenReturn(userPage);
        when(userService.getByUsername(anyString())).thenReturn(user);
        when(userService.isCorrectCredentials(anyString(), anyString())).thenReturn(true);
        when(reviewService.getReviewsByUserId(anyLong(), any(Pageable.class))).thenReturn(reviewPage);

        when(sortUtility.parseSort(anyString())).thenReturn(sort);
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
                .andExpect(model().attribute(USER_PAGE_ATTR, userPage));
    }

    @Test
    void showAllInAdminPanel_shouldMakePageRequestByProperParams_whenRequestContainAppropriateParams() throws Exception {

        when(sortUtility.parseSort(sortStr)).thenReturn(sort);

        mockMvc.perform(get("/users/admin")
                        .param(PAGE_NUMBER_ATTR, String.valueOf(pageNumber))
                        .param(PAGE_SIZE_ATTR, String.valueOf(pageSize))
                        .param(SORT_ATTR, sortStr))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(userService).getAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    @Test
    void showAllInAdminPanel_shouldAddPageNumberAndSortModelAttributes_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/users/admin")
                        .param(PAGE_NUMBER_ATTR, String.valueOf(pageNumber))
                        .param(PAGE_SIZE_ATTR, String.valueOf(pageSize))
                        .param(SORT_ATTR, sortStr))
                .andExpect(status().isOk())
                .andExpect(model().attribute(SORT_ATTR, sortStr));
    }

    @Test
    void showAllInAdminPanel_shouldAddDefaultPageNumberAndSortModelAttributes_whenRequestDoesNotContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/users/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(SORT_ATTR));
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
                .andExpect(model().attribute(USER_ATTR, user));
    }

    @Test
    void showCurrentUserProfile_shouldAddReviewPageModelAttribute() throws Exception {

        mockMvc.perform(get("/users/profile")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute(REVIEW_PAGE_ATTR, reviewPage));
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
                        .param(REVIEW_PAGE_NUMBER_ATTR, String.valueOf(pageNumber))
                        .param(REVIEW_SORT_ATTR, sortStr)
                        .principal(mockAuthentication))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(reviewService).getReviewsByUserId(user.getId(), PageRequest.of(pageNumber, REVIEW_PAGE_SIZE, sort));
    }

    @Test
    void showCurrentUserProfile_shouldAddReviewPageNumberAndSortModelAttributes_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/users/profile")
                        .param(REVIEW_PAGE_NUMBER_ATTR, String.valueOf(pageNumber))
                        .param(REVIEW_SORT_ATTR, sortStr)
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute(REVIEW_SORT_ATTR, sortStr));
    }

    @Test
    void showCurrentUserProfile_shouldAddDefaultReviewPageNumberAndSortModelAttributes_whenRequestDoesNotContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/users/profile")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(REVIEW_SORT_ATTR));
    }

    @Test
    void showCurrentUserProfile_shouldForbidRequest_whenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getByUsername(anyString());
        verify(reviewService, never()).getReviewsByUserId(anyLong(), any(PageRequest.class));
    }

    @Test
    void create_shouldRedirectToAppropriateUrl_ifUserIsValid() throws Exception {

        mockMvc.perform(post("/users/admin")
                        .flashAttr(USER_ATTR, user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/admin"));

    }

    @Test
    void create_shouldNeverCreateUser_ifUserIsInvalid() throws Exception {

        mockMvc.perform(post("/users/admin")
                .flashAttr(USER_ATTR, new User()));

        verify(userService, never()).create(any(User.class));
    }

    @Test
    void create_shouldRedirectToAppropriateUrl_ifUserIsInvalid() throws Exception {

        mockMvc.perform(post("/users/admin")
                        .flashAttr(USER_ATTR, new User()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/admin/new"));
    }

    @Test
    void updateCurrentUserPassword_shouldRedirectToAppropriateUrl() throws Exception {

        mockMvc.perform(patch("/users/profile/password")
                        .flashAttr(PASSWORD_UPDATE_DTO_ATTR, passwordUpdateDto)
                        .principal(mockAuthentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile"));
    }

    @Test
    void updateCurrentUserPassword_shouldCheckCredentialsByProperParams() throws Exception {

        mockMvc.perform(patch("/users/profile/password")
                        .flashAttr(PASSWORD_UPDATE_DTO_ATTR, passwordUpdateDto)
                        .principal(mockAuthentication));

        verify(userService).isCorrectCredentials(USERNAME, passwordUpdateDto.getOldPassword());
    }

    @Test
    void updateCurrentUserPassword_shouldRejectUpdate_whenOldPasswordIsIncorrect() throws Exception {

        when(userService.isCorrectCredentials(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(patch("/users/profile/password")
                        .flashAttr(PASSWORD_UPDATE_DTO_ATTR, passwordUpdateDto)
                        .principal(mockAuthentication))
                .andExpect(model().attributeHasFieldErrors(PASSWORD_UPDATE_DTO_ATTR, OLD_PASSWORD_FIELD_ATTR));

        verify(userService, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void updateCurrentUserPassword_shouldProperlyUpdatePassword() throws Exception {

        mockMvc.perform(patch("/users/profile/password")
                .flashAttr(PASSWORD_UPDATE_DTO_ATTR, passwordUpdateDto)
                .principal(mockAuthentication));

        verify(userService).updatePassword(user.getId(), passwordUpdateDto.getNewPassword());
    }

    @Test
    void updateCurrentUserPassword_shouldForbidRequest_whenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(patch("/users/profile/password")
                        .flashAttr(PASSWORD_UPDATE_DTO_ATTR, passwordUpdateDto))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void updateCurrentUserPassword_shouldNeverUpdatePassword_whenNewPasswordIsInvalid() throws Exception {

        mockMvc.perform(patch("/users/profile/password")
                .flashAttr(PASSWORD_UPDATE_DTO_ATTR, new PasswordUpdateDto(passwordUpdateDto.getOldPassword(), " "))
                .principal(mockAuthentication));

        verify(userService, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    void editCurrentUserPublicName_shouldReturnAppropriateView() throws Exception {

        mockMvc.perform(get("/users/profile/public-name/edit")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(view().name("user/public-name-edit"));
    }

    @Test
    void editCurrentUserPublicName_shouldAddProperDtoModelAttribute() throws Exception {

        mockMvc.perform(get("/users/profile/public-name/edit")
                        .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute(PUBLIC_NAME_UPDATE_DTO_ATTR, publicNameUpdateDto));
    }

    @Test
    void editCurrentUserPublicName_shouldShouldGetUserByProperUsername() throws Exception {

        mockMvc.perform(get("/users/profile/public-name/edit")
                        .principal(mockAuthentication));

        verify(userService).getByUsername(USERNAME);
    }

    @Test
    void editCurrentUserPublicName_shouldForbidRequest_whenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(get("/users/profile/public-name/edit"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getByUsername(anyString());
    }

    @Test
    void updateCurrentUserPublicName_shouldRedirectToAppropriateUrl() throws Exception {

        mockMvc.perform(patch("/users/profile/public-name")
                        .flashAttr(PUBLIC_NAME_UPDATE_DTO_ATTR, publicNameUpdateDto)
                        .principal(mockAuthentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile"));
    }

    @Test
    void updateCurrentUserPublicName_shouldShouldGetUserByProperUsername() throws Exception {

        mockMvc.perform(patch("/users/profile/public-name")
                        .flashAttr(PUBLIC_NAME_UPDATE_DTO_ATTR, publicNameUpdateDto)
                        .principal(mockAuthentication));

        verify(userService).getByUsername(USERNAME);
    }

    @Test
    void updateCurrentUserPublicName_shouldProperlyUpdatePublicName() throws Exception {

        mockMvc.perform(patch("/users/profile/public-name")
                        .flashAttr(PUBLIC_NAME_UPDATE_DTO_ATTR, publicNameUpdateDto)
                        .principal(mockAuthentication));

        verify(userService).updatePublicName(user.getId(), publicNameUpdateDto.getPublicName());
    }

    @Test
    void updateCurrentUserPublicName_shouldForbidRequest_whenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(patch("/users/profile/public-name")
                        .flashAttr(PUBLIC_NAME_UPDATE_DTO_ATTR, publicNameUpdateDto))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updatePublicName(anyLong(), any());
    }

    @Test
    void updateCurrentUserPublicName_shouldNeverUpdatePublicName_whenPublicNameIsInvalid() throws Exception {

        mockMvc.perform(patch("/users/profile/public-name")
                        .flashAttr(PUBLIC_NAME_UPDATE_DTO_ATTR, new PublicNameUpdateDto(" "))
                        .principal(mockAuthentication));

        verify(userService, never()).updatePublicName(anyLong(), any());
    }
}