package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
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

import java.util.Arrays;

import static com.myproject.bikereviewapp.controller.BrandController.BRAND_ATTR;
import static com.myproject.bikereviewapp.controller.BrandController.BRAND_PAGE_ATTR;
import static com.myproject.bikereviewapp.controller.MainController.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BrandController.class)
@AutoConfigureMockMvc(addFilters = false)
class BrandControllerTest {


    @MockBean
    BrandService brandService;

    @MockBean
    SortUtility sortUtility;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BrandController brandController;


    private static Page<Brand> brandPage;
    private static Brand brand;
    private static Long id;
    private static int pageNumber;
    private static int pageSize;
    private static Sort sort;
    private static String sortStr;


    @BeforeAll
    static void init() {
        brandPage = new PageImpl<>(Arrays.asList(
                new Brand(1L, "somename1", "somecountry1"),
                new Brand(2L, "somename2", "somecountry2"),
                new Brand(3L, "somename3", "somecountry3")
        ));

        brand = new Brand(null, "someName", "someCountry");
        id = 10L;

        pageNumber = 7;
        pageSize = 15;
        sort = Sort.by(Sort.Direction.ASC, "id");
        sortStr = "id:asc";
    }

    @BeforeEach
    void setUp() {
        when(sortUtility.parseSort(anyString())).thenReturn(sort);

        when(brandService.getAll(any(PageRequest.class))).thenReturn(brandPage);
    }


    @Test
    void showAllInAdminPanel_shouldReturnAppropriateView() throws Exception {

        mockMvc.perform(get("/brands/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("brand/admin/all"));
    }

    @Test
    void showAllInAdminPanel_shouldAddBrandPageModelAttribute() throws Exception {

        mockMvc.perform(get("/brands/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(BRAND_PAGE_ATTR, brandPage));
    }

    @Test
    void showAllInAdminPanel_shouldMakePageRequestByProperPageNumberAndSort_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/brands/admin")
                        .param(PAGE_NUMBER_ATTR, String.valueOf(pageNumber))
                        .param(PAGE_SIZE_ATTR, String.valueOf(pageSize))
                        .param(SORT_ATTR, sortStr))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(brandService).getAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    @Test
    void showAllInAdminPanel_shouldAddPageNumberAndSortModelAttributes_whenRequestContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/brands/admin")
                        .param(PAGE_NUMBER_ATTR, String.valueOf(pageNumber))
                        .param(PAGE_SIZE_ATTR, String.valueOf(pageSize))
                        .param(SORT_ATTR, sortStr))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPageNumber", pageNumber))
                .andExpect(model().attribute("currentSort", sortStr));
    }

    @Test
    void showAllInAdminPanel_shouldAddDefaultPageNumberAndSortModelAttributes_whenRequestDoesNotContainAppropriateParams() throws Exception {

        mockMvc.perform(get("/brands/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentPageNumber"))
                .andExpect(model().attributeExists("currentSort"));
    }

    @Test
    void create_shouldCreateBrand_ifBrandIsValid() throws Exception {

        mockMvc.perform(post("/brands/admin")
                        .flashAttr(BRAND_ATTR, brand));

        verify(brandService).create(brand);
    }

    @Test
    void create_shouldRedirectToAppropriateUrl_ifBrandIsValid() throws Exception {

        mockMvc.perform(post("/brands/admin")
                .flashAttr(BRAND_ATTR, brand))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands/admin"));

    }

    @Test
    void create_shouldNeverCreateBrand_ifBrandIsInvalid() throws Exception {

        mockMvc.perform(post("/brands/admin")
                        .flashAttr(BRAND_ATTR, new Brand()));

        verify(brandService, never()).create(any(Brand.class));
    }

    @Test
    void create_shouldRedirectToAppropriateUrl_ifBrandIsInvalid() throws Exception {

        mockMvc.perform(post("/brands/admin")
                        .flashAttr(BRAND_ATTR, new Brand()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands/admin/new"));
    }

    @Test
    void update_shouldUpdateBrand_ifBrandIsValid() throws Exception {

        mockMvc.perform(put("/brands/admin/{id}", id)
                .flashAttr(BRAND_ATTR, brand));

        verify(brandService).update(id, brand);
    }

    @Test
    void update_shouldRedirectToAppropriateUrl_ifBrandIsValid() throws Exception {

        mockMvc.perform(put("/brands/admin/{id}", id)
                        .flashAttr(BRAND_ATTR, brand))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands/admin"));

    }

    @Test
    void update_shouldNeverUpdateBrand_ifBrandIsInvalid() throws Exception {

        mockMvc.perform(put("/brands/admin/{id}", id)
                .flashAttr(BRAND_ATTR, new Brand()));

        verify(brandService, never()).update(anyLong(), any(Brand.class));
    }

    @Test
    void update_shouldRedirectToAppropriateUrl_ifBrandIsInvalid() throws Exception {

        mockMvc.perform(put("/brands/admin/{id}", id)
                        .flashAttr(BRAND_ATTR, new Brand()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands/admin/edit/" + id));
    }
}