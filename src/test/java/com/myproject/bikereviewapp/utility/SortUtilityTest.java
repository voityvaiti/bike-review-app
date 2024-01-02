package com.myproject.bikereviewapp.utility;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SortUtility.class)
class SortUtilityTest {

    @Autowired
    SortUtility sortUtility;


    @Test
    void parseSort_shouldShouldReturnAppropriateSort_ifSortExpressionIsValid() {
        Sort.Order idDescOrder = Sort.Order.desc("id");
        Sort.Order nameAscOrder = Sort.Order.asc("name");
        Sort.Order averageAgeDescOrder = Sort.Order.desc("averageAge");
        Sort.Order countryAscOrder = Sort.Order.asc("country");

        assertEquals(Sort.by(idDescOrder), sortUtility.parseSort("id:desc"));
        assertEquals(Sort.by(nameAscOrder), sortUtility.parseSort("name  :asc"));
        assertEquals(Sort.by(averageAgeDescOrder), sortUtility.parseSort("  averageAge:  desc"));
        assertEquals(Sort.by(countryAscOrder), sortUtility.parseSort(" country      :asc    "));

        assertEquals(Sort.by(List.of(idDescOrder, nameAscOrder)), sortUtility.parseSort("id:desc,name:asc"));
        assertEquals(Sort.by(List.of(nameAscOrder, averageAgeDescOrder, countryAscOrder)), sortUtility.parseSort("name: asc, averageAge:desc  ,   country  :asc"));
        assertEquals(Sort.by(List.of(averageAgeDescOrder, countryAscOrder, idDescOrder, nameAscOrder)), sortUtility.parseSort("averageAge  :   desc ,country:asc,id:desc,name:asc "));
    }

    @Test
    void parseSort_shouldReturnDefaultSort_ifSortExpressionIsInvalid() {
        Sort defaultSort = SortUtility.getDefaultSort();

        assertEquals(defaultSort, sortUtility.parseSort(""));
        assertEquals(defaultSort, sortUtility.parseSort("    "));
        assertEquals(defaultSort, sortUtility.parseSort(":"));
        assertEquals(defaultSort, sortUtility.parseSort("id:acss"));
        assertEquals(defaultSort, sortUtility.parseSort("field-asc"));
        assertEquals(defaultSort, sortUtility.parseSort("field1:asc,invalid,field2:desc"));
        assertEquals(defaultSort, sortUtility.parseSort("1235:desc"));
    }

}