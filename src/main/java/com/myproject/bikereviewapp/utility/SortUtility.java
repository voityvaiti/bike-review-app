package com.myproject.bikereviewapp.utility;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SortUtility {

    private final EntityManager entityManager;

    private static final String ORDER_EXPRESSION_REGEX = "^\\s*[A-Za-z.]+\\s*(:\\s*((asc|desc)|)\\s*)?$";

    private static final String SORT_EXPRESSION_DELIMITER = ",";

    private static final String ORDER_EXPRESSION_DELIMITER = ":";
    private static final int ORDER_EXPRESSION_PROPERTY_INDEX = 0;
    private static final int ORDER_EXPRESSION_DIRECTION_INDEX = 1;

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.asc("id"));


    public Sort parseSort(String sortExpression, Class<?> entityClass) {

        validateEntityClass(entityClass);

        if (!isValidSortExpression(sortExpression)) {
            return getDefaultSort();
        }

        List<Sort.Order> orders = Arrays.stream(sortExpression.split(SORT_EXPRESSION_DELIMITER))
                .map(expr -> parseOrderExpression(expr, entityClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return orders.isEmpty() ? getDefaultSort() : Sort.by(orders);
    }

    private Sort.Order parseOrderExpression(String orderExpression, Class<?> entityClass) {

        String[] orderParams = orderExpression.split(ORDER_EXPRESSION_DELIMITER);

        String property = orderParams[ORDER_EXPRESSION_PROPERTY_INDEX].trim();
        String direction = orderParams.length > ORDER_EXPRESSION_DIRECTION_INDEX ?
                orderParams[ORDER_EXPRESSION_DIRECTION_INDEX].trim() : "";

        if (isValidProperty(property, entityClass)) {

            return direction.equalsIgnoreCase("desc") ? Sort.Order.desc(property) : Sort.Order.asc(property);
        } else {
            return null;
        }
    }

    private static boolean isValidSortExpression(String sortExpression) {
        return Optional.ofNullable(sortExpression)
                .map(expression -> !expression.isBlank())
                .orElse(false) && Arrays.stream(sortExpression.split(SORT_EXPRESSION_DELIMITER))
                .allMatch(orderExpression -> Pattern.matches(ORDER_EXPRESSION_REGEX, orderExpression));
    }

    private boolean isValidProperty(String propertyPath, Class<?> entityClass) {

        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<?> entityType = metamodel.entity(entityClass);

        String[] properties = propertyPath.split("\\.");

        ManagedType<?> currentType = entityType;

        for (int i = 0; i < properties.length; i++) {
            String property = properties[i];
            SingularAttribute<?, ?> attribute;

            try {
                attribute = currentType.getSingularAttribute(property);
            } catch (IllegalArgumentException e) {
                return false;
            }

            if (i < properties.length - 1) {

                if (attribute.getType() instanceof ManagedType) {
                    currentType = (ManagedType<?>) attribute.getType();

                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private static Sort getDefaultSort() {
        return DEFAULT_SORT;
    }

    private void validateEntityClass(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Provided class is not a JPA entity: " + entityClass.getName());
        }
    }

}