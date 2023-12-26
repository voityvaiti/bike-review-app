package com.myproject.bikereviewapp.controller;

import com.myproject.bikereviewapp.entity.Brand;
import com.myproject.bikereviewapp.service.abstraction.BrandService;
import com.myproject.bikereviewapp.utility.SortUtility;
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

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BrandController.class)
@AutoConfigureMockMvc(addFilters = false)
class BrandControllerTest {

    private static final String SAMPLE_VIEW = "some/view";

    @MockBean
    BrandService brandService;

    @MockBean
    SortUtility sortUtility;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BrandController brandController;


    @Test
    void showAllInAdminPanel_shouldReturnAppropriateView() throws Exception {

        Sort sampleSort = Sort.by(Sort.Direction.ASC, "name");
        Page<Brand> brandPage = Page.empty();

        when(sortUtility.parseSort(anyString())).thenReturn(sampleSort);
        when(brandService.getAll(any(PageRequest.class))).thenReturn(brandPage);

        mockMvc.perform(get("/brands/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("brand/admin/all"));
    }

    @Test
    void showAllInAdminPanel_shouldAddBrandPageModelAttribute() throws Exception {

        Sort sampleSort = Sort.by(Sort.Direction.ASC, "name");

        Page<Brand> brandPage = new PageImpl<>(Arrays.asList(
                new Brand(1L, "somename1", "somecountry1"),
                new Brand(2L, "somename2", "somecountry2"),
                new Brand(3L, "somename3", "somecountry3")
        ));

        when(sortUtility.parseSort(anyString())).thenReturn(sampleSort);
        when(brandService.getAll(any(PageRequest.class))).thenReturn(brandPage);

        mockMvc.perform(get("/brands/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("brandPage", brandPage));
    }

    @Test
    void showAllInAdminPanel_shouldMakePageRequestByProperPageNumberAndSort_whenQueryContainAppropriateParams() throws Exception {
        int pageNumber = 5;
        int pageSize = 15;
        String sortStr = "name:asc";
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Page<Brand> brandPage = Page.empty();

        when(sortUtility.parseSort(sortStr)).thenReturn(sort);
        when(brandService.getAll(any(PageRequest.class))).thenReturn(brandPage);


        mockMvc.perform(get("/brands/admin")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sortStr))
                .andExpect(status().isOk());

        verify(sortUtility).parseSort(sortStr);
        verify(brandService).getAll(PageRequest.of(pageNumber, pageSize, sort));
    }

    @Test
    void showAllInAdminPanel_shouldAddPageNumberAndSortModelAttributes_whenQueryContainAppropriateParams() throws Exception {
        int pageNumber = 5;
        int pageSize = 15;
        String sortStr = "name:asc";
        Sort sampleSort = Sort.by(Sort.Direction.ASC, "name");

        Page<Brand> brandPage = Page.empty();

        when(sortUtility.parseSort(anyString())).thenReturn(sampleSort);
        when(brandService.getAll(any(PageRequest.class))).thenReturn(brandPage);


        mockMvc.perform(get("/brands/admin")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sort", sortStr))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPageNumber", pageNumber))
                .andExpect(model().attribute("currentSort", sortStr));
    }

    @Test
    void showAllInAdminPanel_shouldAddDefaultPageNumberAndSortModelAttributes_whenQueryDoesNotContainAppropriateParams() throws Exception {

        Sort sampleSort = Sort.by(Sort.Direction.ASC, "name");
        Page<Brand> brandPage = Page.empty();

        when(sortUtility.parseSort(anyString())).thenReturn(sampleSort);
        when(brandService.getAll(any(PageRequest.class))).thenReturn(brandPage);

        mockMvc.perform(get("/brands/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("currentPageNumber"))
                .andExpect(model().attributeExists("currentSort"));
    }

    @Test
    void create_shouldCreateBrand_ifBrandIsValid() {

        Brand brand = new Brand(null, "someName", "someCountry");

        BindingResult mockBindingResult = mock(BindingResult.class);
        BrandController spyBrandController = spy(brandController);

        doReturn(SAMPLE_VIEW).when(spyBrandController).newBrand(any(Brand.class));
        when(brandService.create(any(Brand.class))).thenReturn(new Brand());

        when(mockBindingResult.hasErrors()).thenReturn(false);

        spyBrandController.create(brand, mockBindingResult);

        verify(brandService).create(brand);
    }

    @Test
    void create_shouldNeverCreateBrand_ifBrandIsInvalid() {

        Brand brand = new Brand(null, "someName", "someCountry");

        BindingResult mockBindingResult = mock(BindingResult.class);
        BrandController spyBrandController = spy(brandController);

        doReturn(SAMPLE_VIEW).when(spyBrandController).newBrand(any(Brand.class));
        when(brandService.create(any(Brand.class))).thenReturn(new Brand());

        when(mockBindingResult.hasErrors()).thenReturn(true);

        spyBrandController.create(brand, mockBindingResult);

        verify(brandService, never()).create(any(Brand.class));
    }

    @Test
    void update_shouldUpdateBrand_ifBrandIsValid() {

        Long id = 10L;
        Brand brand = new Brand(id, "someName", "someCountry");

        BindingResult mockBindingResult = mock(BindingResult.class);
        Model mockModel = mock(Model.class);
        BrandController spyBrandController = spy(brandController);

        doReturn(SAMPLE_VIEW).when(spyBrandController).edit(anyLong(), any(Model.class));
        when(brandService.update(anyLong(), any(Brand.class))).thenReturn(new Brand());

        when(mockBindingResult.hasErrors()).thenReturn(false);

        spyBrandController.update(id, brand, mockBindingResult, mockModel);

        verify(brandService).update(id, brand);
    }

    @Test
    void update_shouldNeverUpdateBrand_ifBrandIsInvalid() {

        Long id = 10L;
        Brand brand = new Brand(id, "someName", "someCountry");

        BindingResult mockBindingResult = mock(BindingResult.class);
        Model mockModel = mock(Model.class);
        BrandController spyBrandController = spy(brandController);

        doReturn(SAMPLE_VIEW).when(spyBrandController).edit(anyLong(), any(Model.class));
        when(brandService.update(anyLong(), any(Brand.class))).thenReturn(new Brand());

        when(mockBindingResult.hasErrors()).thenReturn(true);

        spyBrandController.update(id, brand, mockBindingResult, mockModel);

        verify(brandService, never()).update(anyLong(), any(Brand.class));
    }


}