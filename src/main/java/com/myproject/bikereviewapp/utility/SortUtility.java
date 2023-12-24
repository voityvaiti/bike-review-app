package com.myproject.bikereviewapp.utility;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SortUtility {

    private static final String ORDER_EXPRESSION_REGEX = "^\\s*[A-Za-z.]+\\s*(:\\s*((asc|desc)|))?$";

    private static final String SORT_EXPRESSION_DELIMITER = ",";

    private static final String ORDER_EXPRESSION_DELIMITER = ":";
    private static final int ORDER_EXPRESSION_PROPERTY_INDEX = 0;
    private static final int ORDER_EXPRESSION_DIRECTION_INDEX = 1;

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("id"));


    public Sort parseSort(String sortExpression) {

        if (!isValidSortExpression(sortExpression)) {
            return getDefaultSort();
        }

        List<Sort.Order> orders = new ArrayList<>();

        String[] orderExpressions = sortExpression.split(SORT_EXPRESSION_DELIMITER);

        for (String orderExpression : orderExpressions) {
            String[] orderParams = orderExpression.split(ORDER_EXPRESSION_DELIMITER);
            String property = orderParams[ORDER_EXPRESSION_PROPERTY_INDEX].trim();
            String direction = "";

            if (orderParams.length > ORDER_EXPRESSION_DIRECTION_INDEX) {
                direction = orderParams[ORDER_EXPRESSION_DIRECTION_INDEX].trim();
            }

            Sort.Order order = direction.equalsIgnoreCase("desc") ?
                    Sort.Order.desc(property) : Sort.Order.asc(property);

            orders.add(order);
        }

        return Sort.by(orders);
    }

    private static Sort getDefaultSort() {
        return DEFAULT_SORT;
    }

    private static boolean isValidSortExpression(String sortExpression) {

        if (sortExpression != null && !sortExpression.isBlank()) {

            String[] orderExpressions = sortExpression.split(SORT_EXPRESSION_DELIMITER);

            for (String orderExpression: orderExpressions) {
                if(!Pattern.matches(ORDER_EXPRESSION_REGEX, orderExpression)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}