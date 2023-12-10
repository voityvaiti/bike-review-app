package com.myproject.bikereviewapp.utility;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SortUtility {

    private static final String SORT_FIELD_REGEX = "^\\s*[A-Za-z.]+\\s*(:\\s*((asc|desc)|))?$";

    private SortUtility() {
    }

    public static Sort parseSort(String sortExpression) {

        if (!isValidSortExpression(sortExpression)) {
            return getDefaultSort();
        }

        List<Sort.Order> orders = new ArrayList<>();

        String[] sortFields = sortExpression.split(",");

        for (String sortField : sortFields) {
            String[] parts = sortField.split(":");
            String field = parts[0].trim();
            String direction = "";

            if (parts.length > 1) {
                direction = parts[1].trim();
            }

            Sort.Order order = direction.equalsIgnoreCase("desc") ?
                    Sort.Order.desc(field) : Sort.Order.asc(field);

            orders.add(order);
        }

        return Sort.by(orders);
    }

    private static Sort getDefaultSort() {
        return Sort.by(Sort.Order.asc("id"));
    }

    private static boolean isValidSortExpression(String sortExpression) {

        if (sortExpression != null && !sortExpression.isBlank()) {

            String[] sortFields = sortExpression.split(",");

            for (String sortField: sortFields) {
                if(!Pattern.matches(SORT_FIELD_REGEX, sortField)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}