package com.myproject.bikereviewapp.service.abstraction;

public interface EntityService {
    boolean exists(Class<?> entityClass, String fieldName, Object value);
}
