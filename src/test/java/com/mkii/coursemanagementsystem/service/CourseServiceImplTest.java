package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.entity.Course;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImplTest.class);

    @Autowired
    private CourseService courseService;

    private static Long createdCourseId;
    private static final String UNIQUE_SUFFIX = String.valueOf(System.currentTimeMillis() % 1000000);

    // ===================== CREATE =====================

    @Test
    @Order(1)
    public void createTest() {
        CourseRequestDTO requestDTO = CourseRequestDTO.builder()
                .title("Lập trình Java Nâng cao " + UNIQUE_SUFFIX)
                .description("Khóa học lập trình Java Spring Boot chuyên sâu")
                .duration(45)
                .price(1500000.0)
                .build();

        logger.info("Creating course: {}", requestDTO.getTitle());
        Course response = courseService.create(requestDTO);
        logger.info("Created Course response: {}", response);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(requestDTO.getTitle(), response.getTitle());
        assertEquals(1500000.0, response.getPrice());

        createdCourseId = response.getId();
    }

    @Test
    @Order(2)
    void create_NullRequest_ThrowsException() {
        assertThrows(Exception.class, () -> courseService.create(null));
    }

    // ===================== UPDATE =====================

    @Test
    @Order(3)
    void updateCourseTest() {
        assertNotNull(createdCourseId);
        CourseRequestDTO updateDTO = CourseRequestDTO.builder()
                .title("Lập trình Java Nâng cao Updated " + UNIQUE_SUFFIX)
                .description("Nội dung khóa học đã được cập nhật chuẩn kiến trúc")
                .duration(50)
                .price(1800000.0)
                .build();

        Course updated = courseService.update(updateDTO, createdCourseId);
        assertNotNull(updated);
        assertEquals(updateDTO.getTitle(), updated.getTitle());
        assertEquals(1800000.0, updated.getPrice());
        logger.info("Updated Course: {}", updated);
    }

    // ===================== READ / SEARCH =====================

    @Test
    @Order(4)
    void getCourseByIdTest() {
        assertNotNull(createdCourseId);
        CourseResponseDTO response = courseService.getById(createdCourseId);
        assertNotNull(response);
        assertEquals(createdCourseId, response.getId());
        logger.info("Found course by ID: {}", response);
    }

    @Test
    @Order(5)
    void getAllCoursesTest() {
        List<CourseResponseDTO> list = courseService.getAll();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        logger.info("All courses count: {}", list.size());
    }

    @Test
    @Order(6)
    void searchCourses_WithKeyword() {
        List<CourseResponseDTO> list = courseService.searchByTitle("Java");
        assertNotNull(list);
        logger.info("Courses found by title 'Java': {}", list.size());
    }

    @Test
    @Order(7)
    void filterCoursesByPrice() {
        List<CourseResponseDTO> list = courseService.filterByPrice(1000000.0, 2000000.0);
        assertNotNull(list);
        logger.info("Courses in price range 1M-2M: {}", list.size());
    }

    @Test
    @Order(8)
    void getAllCourses_WithPagination() {
        Page<CourseResponseDTO> page = courseService.getAllPaged(0, 5, "price", "desc");
        assertNotNull(page);
        logger.info("Paged courses: totalElements={}, totalPages={}", page.getTotalElements(), page.getTotalPages());
    }

    @Test
    @Order(9)
    void searchCoursesBySpecification() {
        List<CourseResponseDTO> list = courseService.searchBySpecification("title:Java,price>500000");
        assertNotNull(list);
        logger.info("Specification search result count: {}", list.size());
    }

    @Test
    @Order(10)
    void getCourseCount_Success() {
        Integer count = courseService.getCount();
        assertNotNull(count);
        assertTrue(count > 0);
        logger.info("Total course count: {}", count);
    }

    // ===================== DELETE & EDGE CASES =====================

    @Test
    @Order(11)
    void deleteCourseTest() {
        assertNotNull(createdCourseId);
        courseService.delete(createdCourseId);
        logger.info("Deleted course with ID: {}", createdCourseId);

        assertThrows(ResourceNotFoundException.class, () -> courseService.getById(createdCourseId));
    }

    @Test
    @Order(12)
    void getByIdAndTitle_NotFound_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> courseService.getById(999999L));
        assertThrows(ResourceNotFoundException.class, () -> courseService.getByTitle("NonExistentTitle_XYZ"));
        assertThrows(ResourceNotFoundException.class, () -> courseService.update(new CourseRequestDTO(), 999999L));
        assertThrows(ResourceNotFoundException.class, () -> courseService.delete(999999L));
    }

    @Test
    @Order(13)
    void createBatch_SuccessAndEmpty() {
        CourseRequestDTO dto = CourseRequestDTO.builder().title("Batch Course 1").duration(10).price(50.0).build();
        List<CourseResponseDTO> batchResult = courseService.createBatch(List.of(dto));
        assertFalse(batchResult.isEmpty());

        assertTrue(courseService.createBatch(null).isEmpty());
        assertTrue(courseService.createBatch(Collections.emptyList()).isEmpty());
    }

    @Test
    @Order(14)
    void queryMethods_ByField_AndSorted() {
        Course created = courseService.create(CourseRequestDTO.builder().title("Query Test Course").duration(25).price(120.0).build());

        assertNotNull(courseService.getByTitle("Query Test Course"));
        assertNotNull(courseService.getByField("title", "Query Test Course"));
        assertThrows(ResourceNotFoundException.class, () -> courseService.getByField("title", "NotFoundTitle"));

        assertNotNull(courseService.findByFieldList("duration", "25"));
        assertNotNull(courseService.findByFieldPaged("duration", "25", 0, 5, "price", "asc"));
        assertNotNull(courseService.getAllSorted("price", "asc"));
        assertNotNull(courseService.searchBySpecificationSorted("title:Query", "price", "desc"));
        assertNotNull(courseService.searchBySpecificationPaged("title:Query", 0, 5, "price", "asc"));
    }
}
