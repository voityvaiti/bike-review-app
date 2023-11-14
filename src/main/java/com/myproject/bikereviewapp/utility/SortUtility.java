package com.myproject.bikereviewapp.utility;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class SortUtility {

    private SortUtility() {}

    public static Sort parseSort(String[] sortFields) {
        List<Sort.Order> orders = new ArrayList<>();

        for (String sortField : sortFields) {
            String[] parts = sortField.split(":");
            String field = parts[0].trim();
            String direction = parts[1].trim();

            Sort.Order order = direction.equalsIgnoreCase("asc") ?
                    Sort.Order.asc(field) : Sort.Order.desc(field);

            orders.add(order);
        }

        return Sort.by(orders);
    }
}
