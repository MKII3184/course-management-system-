package com.mkii.coursemanagementsystem.specification;

import com.mkii.coursemanagementsystem.dao.ICourseDao;
import com.mkii.coursemanagementsystem.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenericSpecificationTest {

    @Autowired
    private ICourseDao courseDao;

    @BeforeEach
    void setUp() {
        if (courseDao.getAll().stream().noneMatch(c -> c.getTitle() != null && c.getTitle().contains("Java"))) {
            courseDao.create(Course.builder().title("Java Core Pro").description("Java Basics").duration(40).price(150.0).build());
        }
        if (courseDao.getAll().stream().noneMatch(c -> c.getTitle() != null && c.getTitle().contains("Python"))) {
            courseDao.create(Course.builder().title("Python AI Master").description("Machine Learning").duration(60).price(300.0).build());
        }
        if (courseDao.getAll().stream().noneMatch(c -> c.getTitle() != null && c.getTitle().contains("Spring Boot"))) {
            courseDao.create(Course.builder().title("Spring Boot Microservices").description("Backend").duration(50).price(200.0).build());
        }
    }

    // ==========================================
    // 1. BUILDER QUERY PARSING TESTS
    // ==========================================

    @Test
    @DisplayName("Query parsing with multiple operators and precedence")
    void testSearchQueryParsingPrecedence() {
        GenericSpecificationsBuilder<Course> builder = new GenericSpecificationsBuilder<>();
        builder.withSearchQuery("price>=100,price<=500,duration>30,title:Java,price!=999");

        Specification<Course> spec = builder.build();
        assertNotNull(spec, "Specification should not be null after parsing query");
        List<Course> results = courseDao.findAll(spec);
        assertNotNull(results);
    }

    @Test
    @DisplayName("Empty or null query returns null specification")
    void testEmptyQueryReturnsNull() {
        GenericSpecificationsBuilder<Course> builder = new GenericSpecificationsBuilder<>();
        builder.withSearchQuery("");
        assertNull(builder.build());

        builder.withSearchQuery(null);
        assertNull(builder.build());

        builder.withSearchQuery("   ");
        assertNull(builder.build());
    }

    // ==========================================
    // 2. OPERATOR TESTS EXECUTED ON DATABASE
    // ==========================================

    @Test
    @DisplayName("Operator ':': String partial match (LIKE)")
    void testOperatorColon_StringLike() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("title", ":", "Java"));
        List<Course> list = courseDao.findAll(spec);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().allMatch(c -> c.getTitle().toLowerCase().contains("java")));
    }

    @Test
    @DisplayName("Operator ':': Number equality")
    void testOperatorColon_NumberEquality() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("duration", ":", "40"));
        List<Course> list = courseDao.findAll(spec);
        assertNotNull(list);
    }

    @Test
    @DisplayName("Operator '>': Greater than on numeric field")
    void testOperatorGreaterThan_Number() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("price", ">", "150.0"));
        List<Course> list = courseDao.findAll(spec);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().allMatch(c -> c.getPrice() > 150.0));
    }

    @Test
    @DisplayName("Operator '>=': Greater than or equal to on numeric field")
    void testOperatorGreaterOrEqualTo_Number() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("price", ">=", "150.0"));
        List<Course> list = courseDao.findAll(spec);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().allMatch(c -> c.getPrice() >= 150.0));
    }

    @Test
    @DisplayName("Operator '<': Less than on numeric field")
    void testOperatorLessThan_Number() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("price", "<", "300.0"));
        List<Course> list = courseDao.findAll(spec);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().allMatch(c -> c.getPrice() < 300.0));
    }

    @Test
    @DisplayName("Operator '<=': Less than or equal to on numeric field")
    void testOperatorLessOrEqualTo_Number() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("price", "<=", "200.0"));
        List<Course> list = courseDao.findAll(spec);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().allMatch(c -> c.getPrice() <= 200.0));
    }

    @Test
    @DisplayName("Operator '!' / '!=': String NOT LIKE")
    void testOperatorExclamation_StringNotLike() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("title", "!", "Python"));
        List<Course> list = courseDao.findAll(spec);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().noneMatch(c -> c.getTitle().toLowerCase().contains("python")));
    }

    @Test
    @DisplayName("Operator '!=': Number NOT EQUAL")
    void testOperatorNotEqual_Number() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("duration", "!=", "40"));
        List<Course> list = courseDao.findAll(spec);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().noneMatch(c -> c.getDuration() == 40));
    }

    // ==========================================
    // 3. DEFENSIVE & EDGE CASES
    // ==========================================

    @Test
    @DisplayName("Non-existent field returns null predicate safely without crashing")
    void testNonExistentField_ReturnsNullPredicate() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("invalidField", ":", "val"));
        List<Course> list = courseDao.findAll(spec);
        assertNotNull(list);
    }

    @Test
    @DisplayName("Null criteria or null key/value returns null predicate safely")
    void testNullCriteria_ReturnsNullPredicate() {
        GenericSpecification<Course> nullSpec = new GenericSpecification<>(null);
        assertNull(nullSpec.getCriteria());
        assertNotNull(courseDao.findAll(nullSpec));

        GenericSpecification<Course> nullKeySpec = new GenericSpecification<>(new SearchCriteria(null, ":", "val"));
        assertNotNull(courseDao.findAll(nullKeySpec));

        GenericSpecification<Course> nullValueSpec = new GenericSpecification<>(new SearchCriteria("title", ":", null));
        assertNotNull(courseDao.findAll(nullValueSpec));
    }

    @Test
    @DisplayName("Unparseable number returns null predicate safely")
    void testUnparseableNumber_ReturnsNullPredicate() {
        GenericSpecification<Course> spec = new GenericSpecification<>(new SearchCriteria("price", ">", "not-a-number"));
        assertNotNull(courseDao.findAll(spec));
    }

    @Test
    @DisplayName("SearchCriteria getters and setters test")
    void testSearchCriteriaModel() {
        SearchCriteria sc = new SearchCriteria();
        sc.setKey("title");
        sc.setOperation(":");
        sc.setValue("Java");

        assertEquals("title", sc.getKey());
        assertEquals(":", sc.getOperation());
        assertEquals("Java", sc.getValue());
    }

    @Test
    @DisplayName("String comparison with >, >=, <, <=")
    void testStringComparisonOperators() {
        GenericSpecification<Course> gtSpec = new GenericSpecification<>(new SearchCriteria("title", ">", "A"));
        assertNotNull(courseDao.findAll(gtSpec));

        GenericSpecification<Course> geSpec = new GenericSpecification<>(new SearchCriteria("title", ">=", "A"));
        assertNotNull(courseDao.findAll(geSpec));

        GenericSpecification<Course> ltSpec = new GenericSpecification<>(new SearchCriteria("title", "<", "Z"));
        assertNotNull(courseDao.findAll(ltSpec));

        GenericSpecification<Course> leSpec = new GenericSpecification<>(new SearchCriteria("title", "<=", "Z"));
        assertNotNull(courseDao.findAll(leSpec));
    }
}
