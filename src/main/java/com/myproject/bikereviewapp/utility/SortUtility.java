package com.myproject.bikereviewapp.utility;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class SortUtility {

    private SortUtility() {}

    public static Sort parseSort(String sortExpression) {
        List<Sort.Order> orders = new ArrayList<>();

        String[] sortFields = sortExpression.split(",");

        for (String sortField : sortFields) {
            String[] parts = sortField.split(":");
            String field = parts[0].trim();
            String direction = "";

            if(parts.length > 1) {
                direction = parts[1].trim();
            }

            Sort.Order order = direction.equalsIgnoreCase("desc") ?
                    Sort.Order.desc(field) : Sort.Order.asc(field);

            orders.add(order);
        }

        return Sort.by(orders);
    }
}
