package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Student;
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
public class StudentServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImplTest.class);

    @Autowired
    private StudentService studentService;

    private static Long createdStudentId;
    private static final String UNIQUE_EMAIL = "student_" + (System.currentTimeMillis() % 1000000) + "@vju.edu.vn";

    // ===================== CREATE =====================

    @Test
    @Order(1)
    public void createTest() {
        StudentRequestDTO requestDTO = StudentRequestDTO.builder()
                .fullName("Nguyen Van A")
                .email(UNIQUE_EMAIL)
                .phone("0987654321")
                .address("Hanoi, Vietnam")
                .build();

        logger.info("Creating student: {}", requestDTO.getFullName());
        Student response = studentService.create(requestDTO);
        logger.info("Created Student response: {}", response);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Nguyen Van A", response.getFullName());
        assertEquals(UNIQUE_EMAIL, response.getEmail());

        createdStudentId = response.getId();
    }

    @Test
    @Order(2)
    void create_NullRequest_ThrowsException() {
        assertThrows(Exception.class, () -> studentService.create(null));
    }

    // ===================== UPDATE =====================

    @Test
    @Order(3)
    void updateStudentTest() {
        assertNotNull(createdStudentId);
        StudentRequestDTO updateDTO = StudentRequestDTO.builder()
                .fullName("Nguyen Van A Updated")
                .email(UNIQUE_EMAIL)
                .phone("0912345678")
                .address("Danang, Vietnam")
                .build();

        Student updated = studentService.update(updateDTO, createdStudentId);
        assertNotNull(updated);
        assertEquals("Nguyen Van A Updated", updated.getFullName());
        assertEquals("0912345678", updated.getPhone());
        logger.info("Updated Student: {}", updated);
    }

    // ===================== READ / SEARCH =====================

    @Test
    @Order(4)
    void getStudentByIdTest() {
        assertNotNull(createdStudentId);
        StudentResponseDTO response = studentService.getById(createdStudentId);
        assertNotNull(response);
        assertEquals(createdStudentId, response.getId());
        logger.info("Found student by ID: {}", response);
    }

    @Test
    @Order(5)
    void getStudentByEmailTest() {
        StudentResponseDTO response = studentService.getByEmail(UNIQUE_EMAIL);
        assertNotNull(response);
        assertEquals(UNIQUE_EMAIL, response.getEmail());
        logger.info("Found student by email: {}", response);
    }

    @Test
    @Order(6)
    void getAllStudentsTest() {
        List<StudentResponseDTO> list = studentService.getAll();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        logger.info("All students count: {}", list.size());
    }

    @Test
    @Order(7)
    void searchStudents_WithKeyword() {
        List<StudentResponseDTO> list = studentService.searchByKeyword("Nguyen");
        assertNotNull(list);
        logger.info("Students found by keyword 'Nguyen': {}", list.size());
    }

    @Test
    @Order(8)
    void getAllStudents_WithPagination() {
        Page<StudentResponseDTO> page = studentService.getAllPaged(0, 5, "fullName", "asc");
        assertNotNull(page);
        logger.info("Paged students: totalElements={}, totalPages={}", page.getTotalElements(), page.getTotalPages());
    }

    @Test
    @Order(9)
    void searchStudentsBySpecification() {
        List<StudentResponseDTO> list = studentService.searchBySpecification("fullName:Nguyen");
        assertNotNull(list);
        logger.info("Specification search result count: {}", list.size());
    }

    @Test
    @Order(10)
    void getStudentCount_Success() {
        Integer count = studentService.getCount();
        assertNotNull(count);
        assertTrue(count > 0);
        logger.info("Total student count: {}", count);
    }

    // ===================== DELETE & EDGE CASES =====================

    @Test
    @Order(11)
    void deleteStudentTest() {
        assertNotNull(createdStudentId);
        studentService.delete(createdStudentId);
        logger.info("Deleted student with ID: {}", createdStudentId);

        assertThrows(ResourceNotFoundException.class, () -> studentService.getById(createdStudentId));
    }

    @Test
    @Order(12)
    void getByIdAndEmail_NotFound_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> studentService.getById(999999L));
        assertThrows(ResourceNotFoundException.class, () -> studentService.getByEmail("nonexistent_email@domain.com"));
        assertThrows(ResourceNotFoundException.class, () -> studentService.update(new StudentRequestDTO(), 999999L));
        assertThrows(ResourceNotFoundException.class, () -> studentService.delete(999999L));
    }

    @Test
    @Order(13)
    void createBatch_SuccessAndEmpty() {
        StudentRequestDTO dto = StudentRequestDTO.builder().fullName("Batch Student 1").email("batch_s1_" + System.currentTimeMillis() + "@gmail.com").build();
        List<StudentResponseDTO> batchResult = studentService.createBatch(List.of(dto));
        assertFalse(batchResult.isEmpty());

        assertTrue(studentService.createBatch(null).isEmpty());
        assertTrue(studentService.createBatch(Collections.emptyList()).isEmpty());
    }

    @Test
    @Order(14)
    void queryMethods_ByField_AndSorted() {
        String testEmail = "query_student_" + System.currentTimeMillis() + "@gmail.com";
        studentService.create(StudentRequestDTO.builder().fullName("Query Test Student").email(testEmail).phone("0988776655").address("Hue").build());

        assertNotNull(studentService.getByEmail(testEmail));
        assertNotNull(studentService.getByField("email", testEmail));
        assertThrows(ResourceNotFoundException.class, () -> studentService.getByField("email", "notfound@domain.com"));

        assertNotNull(studentService.findByFieldList("address", "Hue"));
        assertNotNull(studentService.findByFieldPaged("address", "Hue", 0, 5, "fullName", "asc"));
        assertNotNull(studentService.getAllSorted("fullName", "asc"));
        assertNotNull(studentService.searchBySpecificationSorted("fullName:Query", "fullName", "desc"));
        assertNotNull(studentService.searchBySpecificationPaged("fullName:Query", 0, 5, "fullName", "asc"));
    }
}
