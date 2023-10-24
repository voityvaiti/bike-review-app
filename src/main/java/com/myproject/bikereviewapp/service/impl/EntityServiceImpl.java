package com.myproject.bikereviewapp.service.impl;

import com.myproject.bikereviewapp.service.abstraction.EntityService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

@Service
public class EntityServiceImpl implements EntityService {

    private final EntityManager entityManager;

    public EntityServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean exists(Class<?> entityClass, String fieldName, Object value) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<?> root = query.from(entityClass);

        query.select(criteriaBuilder.count(root));
        query.where(criteriaBuilder.equal(root.get(fieldName), value));

        Long count = entityManager.createQuery(query).getSingleResult();

        return count > 0;
    }
}
