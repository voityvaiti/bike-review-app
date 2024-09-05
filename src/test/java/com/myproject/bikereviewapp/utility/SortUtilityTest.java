package com.myproject.bikereviewapp.utility;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SortUtility.class)
class SortUtilityTest {

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private Metamodel metamodel;

    private SortUtility sortUtility;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sortUtility = new SortUtility(entityManager);
        when(entityManager.getMetamodel()).thenReturn(metamodel);
    }


    @Test
    public void testParseSort_WithValidSortExpression_ShouldReturnCorrectSort() {

        String sortExpression = "name:desc,age:asc";
        Class<TestEntity> entityClass = TestEntity.class;

        EntityType<TestEntity> mockEntityType = mock(EntityType.class);
        when(metamodel.entity(entityClass)).thenReturn(mockEntityType);
        when(mockEntityType.getSingularAttribute("name")).thenReturn(mockSingularAttribute());
        when(mockEntityType.getSingularAttribute("age")).thenReturn(mockSingularAttribute());

        Sort sort = sortUtility.parseSort(sortExpression, entityClass);

        assertEquals(2, sort.stream().count());
        assertEquals(Sort.Direction.DESC, Objects.requireNonNull(sort.getOrderFor("name")).getDirection());
        assertEquals(Sort.Direction.ASC, Objects.requireNonNull(sort.getOrderFor("age")).getDirection());
    }

    @Test
    public void testParseSort_WithInvalidSortExpression_ShouldReturnDefaultSort() {
        String invalidSortExpression = "invalid_sort_expression";
        Sort sort = sortUtility.parseSort(invalidSortExpression, TestEntity.class);

        assertEquals(Sort.by("id"), sort);
    }

    @Test
    public void testParseSort_WithBlankSortExpression_ShouldReturnDefaultSort() {
        String blankSortExpression = "";
        Sort sort = sortUtility.parseSort(blankSortExpression, TestEntity.class);

        assertEquals(Sort.by("id"), sort);
    }

    @Test
    public void testParseSort_WithInvalidProperty_ShouldReturnDefaultSort() {
        
        String sortExpression = "invalidProperty:asc";
        Class<TestEntity> entityClass = TestEntity.class;

        EntityType<TestEntity> entityType = mock(EntityType.class);

        when(metamodel.entity(entityClass)).thenReturn(entityType);
        when(entityType.getSingularAttribute("invalidProperty")).thenThrow(IllegalArgumentException.class);

        Sort sort = sortUtility.parseSort(sortExpression, entityClass);

        assertEquals(Sort.by("id"), sort);
    }


    private <X, Y> jakarta.persistence.metamodel.SingularAttribute<X, Y> mockSingularAttribute() {
        return mock(jakarta.persistence.metamodel.SingularAttribute.class);
    }


    @Entity
    public static class TestEntity {

        @Id
        private Long id;

        private String name;
        private int age;
    }

}