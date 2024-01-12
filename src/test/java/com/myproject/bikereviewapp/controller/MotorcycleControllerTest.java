package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.entity.Motorcycle;
import com.myproject.bikereviewapp.entity.Review;
import com.myproject.bikereviewapp.entity.User;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.service.abstraction.MotorcycleService;
import com.myproject.bikereviewapp.service.abstraction.ReviewService;
import com.myproject.bikereviewapp.utility.SortUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(MotorcycleController.class)
@AutoConfigureMockMvc(addFilters = false)
class MotorcycleControllerTest {

    private static final String SAMPLE_VIEW = "some/view";

    @MockBean
    MotorcycleService motorcycleService;

    @MockBean
    BrandService brandService;

    @MockBean
    ReviewService reviewService;

    @MockBean
    SortUtility sortUtility;

    @MockBean
    Model model;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MotorcycleController motorcycleController;


    private static Page<Motorcycle> motorcyclePage;
    private static Page<Review> reviewPage;
    private static Long id;
    private static Motorcycle motorcycle;
    private static int pageNumber;
    private static int pageSize;
    private static Sort sort;
    private static String sortStr;
    private static BindingResult mockBindingResult;

    @BeforeAll
    static void init() {

        motorcyclePage = new PageImpl<>(Arrays.asList(
                new Motorcycle(1L, "model1", new Brand()),
                new Motorcycle(2L, "model2", new Brand()),
                new Motorcycle(3L, "model3", new Brand())
        ));

        reviewPage = new PageImpl<>(Arrays.asList(
                new Review(1L, "body1", LocalDate.of(2000, 1, 1), (short) 1, new Motorcycle(), new User()),
                new Review(2L, "body2", LocalDate.of(2010, 2, 2), (short) 2, new Motorcycle(), new User()),
                new Review(3L, "body3", LocalDate.of(2020, 3, 3), (short) 3, new Motorcycle(), new User())
        ));

        id = 12L;
        motorcycle = new Motorcycle(id, "model", new Brand());

        pageNumber = 2;
        pageSize = 10;
        sort = Sort.by(Sort.Direction.ASC, "id");
        sortStr = "id:asc";
        mockBindingResult = mock(BindingResult.class);
    }

    @BeforeEach
    void setUp() {
        when(motorcycleService.getAll(any(PageRequest.class))).thenReturn(motorcyclePage);
        when(motorcycleService.getById(anyLong())).thenReturn(motorcycle);
        when(reviewService.getReviewsByMotorcycleId(anyLong(), any(PageRequest.class))).thenReturn(reviewPage);

        when(sortUtility.parseSort(anyString())).thenReturn(sort);
        when(mockBindingResult.hasErrors()).thenReturn(false);
    }

    @Test
    void showAll_shouldReturnAppropriateView() throws Exception {

        mockMvc.perform(get("/motorcycles"))
                .andExpect(status().isOk())
                .andExpect(view().name("motorcycle/all"));
    }

    @Test
    void showAll_shouldAddMotorcyclePageModelAttribute() throws Exception {

        mockMvc.perform(get("/motorcycles"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("motorcyclePage", motorcyclePage));
    }

    @Test
    void showAll_shouldMakePageRequestByProperParams_whenRequestContainAppropriateParams() throws Exception {

        when(sortUtility.parseSort(sortStr)).thenReturn(sort);

        mockMvc.perform(get("/motorcycles")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sortStr))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(motorcycleService).getAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    @Test
    void showAll_shouldAddPageNumberAndSortModelAttributes_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/motorcycles")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sortStr))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPageNumber", pageNumber))
                .andExpect(model().attribute("currentSort", sortStr));
    }

    @Test
    void showAll_shouldAddDefaultPageNumberAndSortModelAttributes_whenRequestDoesNotContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/motorcycles"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentPageNumber"))
                .andExpect(model().attributeExists("currentSort"));
    }

    @Test
    void showAllInAdminPanel_shouldReturnAppropriateView() throws Exception {

        mockMvc.perform(get("/motorcycles/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("motorcycle/admin/all"));
    }

    @Test
    void showAllInAdminPanel_shouldAddMotorcyclePageModelAttribute() throws Exception {

        mockMvc.perform(get("/motorcycles/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("motorcyclePage", motorcyclePage));
    }

    @Test
    void showAllInAdminPanel_shouldMakePageRequestByProperParams_whenRequestContainAppropriateParams() throws Exception {

        when(sortUtility.parseSort(sortStr)).thenReturn(sort);

        mockMvc.perform(get("/motorcycles/admin")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sortStr))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(motorcycleService).getAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    @Test
    void showAllInAdminPanel_shouldAddPageNumberAndSortModelAttributes_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/motorcycles/admin")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sortStr))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPageNumber", pageNumber))
                .andExpect(model().attribute("currentSort", sortStr));
    }

    @Test
    void showAllInAdminPanel_shouldAddDefaultPageNumberAndSortModelAttributes_whenRequestDoesNotContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/motorcycles/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentPageNumber"))
                .andExpect(model().attributeExists("currentSort"));
    }


    @Test
    void show_shouldReturnAppropriateView() throws Exception {

        mockMvc.perform(get("/motorcycles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("motorcycle/show"));
    }

    @Test
    void show_shouldAddMotorcycleModelAttribute() throws Exception {

        mockMvc.perform(get("/motorcycles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(model().attribute("motorcycle", motorcycle));
    }

    @Test
    void show_shouldGetMotorcycleByProperId() throws Exception {

        when(motorcycleService.getById(id)).thenReturn(motorcycle);

        mockMvc.perform(get("/motorcycles/{id}", id))
                .andExpect(status().isOk());

        verify(motorcycleService).getById(id);
    }

    @Test
    void show_shouldAddReviewPageModelAttribute() throws Exception {

        mockMvc.perform(get("/motorcycles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(model().attribute("reviewPage", reviewPage));
    }

    @Test
    void show_shouldGetReviewPageByProperMotorcycleIdAndPageRequest_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/motorcycles/{id}", id)
                        .param("reviewPageNumber", String.valueOf(pageNumber))
                        .param("reviewPageSize", String.valueOf(pageSize))
                        .param("reviewSort", sortStr))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(reviewService).getReviewsByMotorcycleId(id, PageRequest.of(pageNumber, pageSize, sort));
    }

    @Test
    void show_shouldAddReviewPageNumberAndSortModelAttributes_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/motorcycles/{id}", id)
                        .param("reviewPageNumber", String.valueOf(pageNumber))
                        .param("reviewPageSize", String.valueOf(pageSize))
                        .param("reviewSort", sortStr))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentReviewPageNumber", pageNumber))
                .andExpect(model().attribute("currentReviewSort", sortStr));
    }

    @Test
    void show_shouldAddDefaultReviewPageNumberAndSortModelAttributes_whenRequestDoesNotContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/motorcycles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentReviewPageNumber"))
                .andExpect(model().attributeExists("currentReviewSort"));
    }


    @Test
    void create_shouldCreateMotorcycle_ifMotorcycleIsValid() {

        motorcycleController.create(motorcycle, mockBindingResult, model);

        verify(motorcycleService).create(motorcycle);
    }

    @Test
    void create_shouldNeverCreateMotorcycle_ifMotorcycleIsInvalid() {

        MotorcycleController spyMotorcycleController = spy(motorcycleController);
        doReturn(SAMPLE_VIEW).when(spyMotorcycleController).newMotorcycle(any(Motorcycle.class), any(Model.class));

        when(mockBindingResult.hasErrors()).thenReturn(true);

        spyMotorcycleController.create(motorcycle, mockBindingResult, model);

        verify(motorcycleService, never()).create(any(Motorcycle.class));
    }

    @Test
    void update_shouldUpdateMotorcycle_ifMotorcycleIsValid() {

        motorcycleController.update(id, motorcycle, mockBindingResult, model);

        verify(motorcycleService).update(id, motorcycle);
    }

    @Test
    void update_shouldNeverUpdateMotorcycle_ifMotorcycleIsInvalid() {

        MotorcycleController spyMotorcycleController = spy(motorcycleController);
        doReturn(SAMPLE_VIEW).when(spyMotorcycleController).edit(anyLong(), any(Model.class));

        when(mockBindingResult.hasErrors()).thenReturn(true);

        spyMotorcycleController.update(id, motorcycle, mockBindingResult, model);

        verify(motorcycleService, never()).update(anyLong(), any(Motorcycle.class));
    }

}