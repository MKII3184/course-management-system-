package com.mkii.coursemanagementsystem.dao;

import com.mkii.coursemanagementsystem.entity.Course;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class HibernateDaoTest {

    @Autowired
    private ICourseDao courseDao;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    // ==========================================
    // 1. DEFENSIVE NULL ARGUMENT CHECKS
    // ==========================================

    @Test
    @DisplayName("create(null) throws exception")
    void testCreateNullEntity_ThrowsException() {
        assertThrows(Exception.class, () -> courseDao.create(null));
    }

    @Test
    @DisplayName("update(null) throws exception")
    void testUpdateNullEntity_ThrowsException() {
        assertThrows(Exception.class, () -> courseDao.update(null));
    }

    @Test
    @DisplayName("saveOrUpdate(null) throws exception")
    void testSaveOrUpdateNullEntity_ThrowsException() {
        assertThrows(Exception.class, () -> courseDao.saveOrUpdate(null));
    }

    @Test
    @DisplayName("delete((Course) null) throws exception")
    void testDeleteNullEntity_ThrowsException() {
        assertThrows(Exception.class, () -> courseDao.delete((Course) null));
    }

    @Test
    @DisplayName("deleteById(null) throws exception")
    void testDeleteNullId_ThrowsException() {
        assertThrows(Exception.class, () -> courseDao.deleteById(null));
    }

    @Test
    @DisplayName("deleteByPk(null) throws exception")
    void testDeleteNullPk_ThrowsException() {
        assertThrows(Exception.class, () -> courseDao.deleteByPk(null));
    }

    @Test
    @DisplayName("get(null) and findById(null) return null / Optional.empty")
    void testGetAndFindNullId_ReturnsEmpty() {
        assertNull(courseDao.get(null));
        assertEquals(Optional.empty(), courseDao.findById(null));
    }

    @Test
    @DisplayName("exists(null) returns false")
    void testExistsNull_ReturnsFalse() {
        assertFalse(courseDao.exists(null));
    }

    // ==========================================
    // 2. SQL INJECTION PREVENTION (isValidIdentifier)
    // ==========================================

    @Test
    @DisplayName("getByField with invalid SQL identifier returns null safely")
    void testGetByField_InvalidIdentifier_ReturnsNull() {
        Course result = courseDao.getByField("title; DROP TABLE courses--", "val");
        assertNull(result);
    }

    @Test
    @DisplayName("findByField with invalid SQL identifier returns empty list safely")
    void testFindByField_InvalidIdentifier_ReturnsEmptyList() {
        List<Course> result = courseDao.findByField("title OR 1=1", "val");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByFieldSorted with invalid SQL identifier returns empty list safely")
    void testFindByFieldSorted_InvalidIdentifier_ReturnsEmptyList() {
        List<Course> result = courseDao.findByFieldSorted("title; select *", "val", Sort.unsorted());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByFieldPaged with invalid SQL identifier returns empty page safely")
    void testFindByFieldPaged_InvalidIdentifier_ReturnsEmptyPage() {
        Page<Course> result = courseDao.findByFieldPaged("title-- injection", "val", PageRequest.of(0, 10));
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // ==========================================
    // 3. SORTING AND PAGINATION (buildSort & buildOrderClause)
    // ==========================================

    @Test
    @DisplayName("findAllSorted: asc, desc, invalid sortBy, null sortBy")
    void testFindAllSorted_Variations() {
        Course c1 = courseDao.create(Course.builder().title("Course A").duration(10).price(100.0).build());
        Course c2 = courseDao.create(Course.builder().title("Course B").duration(20).price(200.0).build());

        // Normal asc
        List<Course> ascList = courseDao.findAllSorted("title", "asc");
        assertFalse(ascList.isEmpty());

        // Normal desc
        List<Course> descList = courseDao.findAllSorted("title", "desc");
        assertFalse(descList.isEmpty());

        // Null sortBy defaults to unsorted
        List<Course> unsortedNull = courseDao.findAllSorted(null, "asc");
        assertFalse(unsortedNull.isEmpty());

        // Blank sortBy defaults to unsorted
        List<Course> unsortedBlank = courseDao.findAllSorted("   ", "asc");
        assertFalse(unsortedBlank.isEmpty());

        // SQL injection in sortBy defaults to unsorted
        List<Course> unsortedInjection = courseDao.findAllSorted("title; DROP TABLE courses", "asc");
        assertFalse(unsortedInjection.isEmpty());
    }

    @Test
    @DisplayName("findAllPaged with valid sort and pagination")
    void testFindAllPaged() {
        Page<Course> page = courseDao.findAllPaged(0, 5, "price", "desc");
        assertNotNull(page);
    }

    @Test
    @DisplayName("searchBySpecificationSorted with valid and empty search query")
    void testSearchBySpecificationSorted() {
        List<Course> list = courseDao.searchBySpecificationSorted("title:Course", "title", "asc");
        assertNotNull(list);

        List<Course> listNull = courseDao.searchBySpecificationSorted("", "title", "asc");
        assertNotNull(listNull);
    }

    @Test
    @DisplayName("searchBySpecificationPaged with valid and empty search query")
    void testSearchBySpecificationPaged() {
        Page<Course> page = courseDao.searchBySpecificationPaged("price>50", 0, 5, "price", "desc");
        assertNotNull(page);

        Page<Course> pageNull = courseDao.searchBySpecificationPaged("", 0, 5, "price", "desc");
        assertNotNull(pageNull);
    }

    // ==========================================
    // 4. BATCH & UTILITY OPERATIONS
    // ==========================================

    @Test
    @DisplayName("createAll and deleteAll with null and empty lists")
    void testCreateAllAndDeleteAll_NullOrEmpty() {
        assertTrue(courseDao.createAll(null).isEmpty());
        assertTrue(courseDao.createAll(Collections.emptyList()).isEmpty());

        assertDoesNotThrow(() -> courseDao.deleteAll(null));
        assertDoesNotThrow(() -> courseDao.deleteAll(Collections.emptyList()));
    }

    @Test
    @DisplayName("count, exists, flush and clear operations")
    void testDaoUtilities() {
        Course c = courseDao.create(Course.builder().title("Utility Test").duration(15).price(50.0).build());

        assertTrue(courseDao.exists(c.getId()));
        assertTrue(courseDao.count() > 0);
        assertDoesNotThrow(() -> courseDao.flush());
        assertDoesNotThrow(() -> courseDao.clear());
    }

    @Test
    @DisplayName("getAll with Sort and Pageable")
    void testGetAllOverloads() {
        assertNotNull(courseDao.getAll(Sort.by("title").ascending()));
        assertNotNull(courseDao.getAll(PageRequest.of(0, 5)));
        assertNotNull(courseDao.getAll());
    }

    @Test
    @DisplayName("findByHql and executeHql")
    void testHqlOperations() {
        com.mkii.coursemanagementsystem.hibernateDao.HibernateGenericDao<Long, Course> genericDao =
                new com.mkii.coursemanagementsystem.hibernateDao.HibernateGenericDao<>(Course.class, entityManager);
        assertNotNull(genericDao.findByHql("SELECT c FROM Course c WHERE c.price > :p", java.util.Map.of("p", 10.0)));
        assertDoesNotThrow(() -> genericDao.executeHql("UPDATE Course c SET c.price = c.price WHERE c.price > :p", java.util.Map.of("p", 10.0)));
    }

    @Test
    @DisplayName("findByFieldPaged and findByFieldSorted")
    void testFindByFieldPagedAndSorted() {
        assertNotNull(courseDao.findByFieldSorted("duration", 40, "price", "asc"));
        assertNotNull(courseDao.findByFieldPaged("duration", 40, 0, 5, "price", "asc"));
        assertNotNull(courseDao.findByFieldPaged("invalid!field", 40, PageRequest.of(0, 5)));
    }

    @Test
    @DisplayName("DAO getters and setters")
    void testDaoGettersAndSetters() {
        com.mkii.coursemanagementsystem.hibernateDao.HibernateGenericDao<Long, Course> genericDao =
                new com.mkii.coursemanagementsystem.hibernateDao.HibernateGenericDao<>(Course.class, entityManager);
        assertEquals(Course.class, genericDao.getType());
        assertNotNull(genericDao.getEntityManager());
        genericDao.setType(Course.class);
        genericDao.setEntityManager(genericDao.getEntityManager());
    }
}
