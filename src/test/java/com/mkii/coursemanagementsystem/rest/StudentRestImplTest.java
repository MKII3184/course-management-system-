package com.mkii.coursemanagementsystem.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Student;
import com.mkii.coursemanagementsystem.service.MinioStorageService;
import com.mkii.coursemanagementsystem.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentRestImplTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private MinioStorageService minioStorageService;

    // ==========================================
    // 1. CREATE STUDENT
    // ==========================================

    @Test
    @DisplayName("Admin creates student successfully - 201 Created")
    void createStudent_Admin_Success() throws Exception {
        StudentRequestDTO requestDTO = StudentRequestDTO.builder()
                .fullName("Nguyen Van A")
                .email("nguyenvana@gmail.com")
                .phone("0912345678")
                .address("Hanoi")
                .build();

        Student mockStudent = Student.builder()
                .id(1L)
                .fullName("Nguyen Van A")
                .email("nguyenvana@gmail.com")
                .phone("0912345678")
                .address("Hanoi")
                .build();

        when(studentService.create(any(StudentRequestDTO.class))).thenReturn(mockStudent);

        mockMvc.perform(post(ApiConstants.BASE_STUDENT_URL + "/create")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Nguyen Van A"));
    }

    @Test
    @DisplayName("Student cannot create student - 403 Forbidden")
    void createStudent_Student_Forbidden() throws Exception {
        StudentRequestDTO requestDTO = StudentRequestDTO.builder()
                .fullName("Test Student")
                .email("test@example.com")
                .build();

        mockMvc.perform(post(ApiConstants.BASE_STUDENT_URL + "/create")
                        .with(httpBasic("student", "student123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create student validation error - 400 Bad Request")
    void createStudent_ValidationError_BadRequest() throws Exception {
        StudentRequestDTO invalidDTO = StudentRequestDTO.builder()
                .fullName("") // Blank fullName
                .email("invalid-email-format") // Invalid email
                .phone("123") // Invalid phone regexp
                .build();

        mockMvc.perform(post(ApiConstants.BASE_STUDENT_URL + "/create")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // 2. UPDATE STUDENT
    // ==========================================

    @Test
    @DisplayName("Admin updates student successfully - 200 OK")
    void updateStudent_Admin_Success() throws Exception {
        StudentRequestDTO requestDTO = StudentRequestDTO.builder()
                .fullName("Nguyen Van Updated")
                .email("updated@gmail.com")
                .phone("0987654321")
                .address("Da Nang")
                .build();

        Student updated = Student.builder()
                .id(1L)
                .fullName("Nguyen Van Updated")
                .email("updated@gmail.com")
                .phone("0987654321")
                .address("Da Nang")
                .build();

        when(studentService.update(any(StudentRequestDTO.class), eq(1L))).thenReturn(updated);

        mockMvc.perform(put(ApiConstants.BASE_STUDENT_URL + "/1")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Nguyen Van Updated"));
    }

    @Test
    @DisplayName("Student cannot update student - 403 Forbidden")
    void updateStudent_Student_Forbidden() throws Exception {
        StudentRequestDTO requestDTO = StudentRequestDTO.builder()
                .fullName("Test")
                .email("test@gmail.com")
                .build();

        mockMvc.perform(put(ApiConstants.BASE_STUDENT_URL + "/update/1")
                        .with(httpBasic("student", "student123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 3. DELETE STUDENT
    // ==========================================

    @Test
    @DisplayName("Admin deletes student successfully - 200 OK")
    void deleteStudent_Admin_Success() throws Exception {
        when(studentService.delete(1L)).thenReturn(Map.of(MessageConstants.STATUS, MessageConstants.SUCCESS, MessageConstants.MESSAGE, MessageConstants.STUDENT_DELETED_SUCCESS));

        mockMvc.perform(delete(ApiConstants.BASE_STUDENT_URL + "/1")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(MessageConstants.SUCCESS));
    }

    @Test
    @DisplayName("Lecturer cannot delete student - 403 Forbidden")
    void deleteStudent_Lecturer_Forbidden() throws Exception {
        mockMvc.perform(delete(ApiConstants.BASE_STUDENT_URL + "/1")
                        .with(httpBasic("lecturer", "lecturer123")))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 4. GET BY ID & NOT FOUND
    // ==========================================

    @Test
    @DisplayName("Get student by ID - 200 OK")
    void getStudentById_Success() throws Exception {
        StudentResponseDTO responseDTO = StudentResponseDTO.builder()
                .id(1L)
                .fullName("Le Thi B")
                .email("lethib@gmail.com")
                .phone("0912345678")
                .address("HCM")
                .build();

        when(studentService.getById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/1")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Le Thi B"));
    }

    @Test
    @DisplayName("Get student by non-existing ID - 404 Not Found")
    void getStudentById_NotFound() throws Exception {
        when(studentService.getById(999L)).thenThrow(new ResourceNotFoundException("Student not found with id: 999"));

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/999")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Student not found with id: 999"));
    }

    // ==========================================
    // 5. GET ALL & BATCH CREATE
    // ==========================================

    @Test
    @DisplayName("Get all students - 200 OK")
    void getAllStudents_Success() throws Exception {
        StudentResponseDTO s1 = StudentResponseDTO.builder().id(1L).fullName("Student 1").build();
        when(studentService.getAll()).thenReturn(List.of(s1));

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL)
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Student 1"));
    }

    @Test
    @DisplayName("Get student by email - 200 OK")
    void getStudentByEmail_Success() throws Exception {
        StudentResponseDTO s = StudentResponseDTO.builder().id(1L).email("john@gmail.com").build();
        when(studentService.getByEmail("john@gmail.com")).thenReturn(s);

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/by-email?queryEmail=john@gmail.com")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@gmail.com"));
    }

    @Test
    @DisplayName("Batch create students - 201 Created")
    void createStudentsBatch_Admin_Success() throws Exception {
        StudentRequestDTO req = StudentRequestDTO.builder().fullName("Batch Student").email("batch@gmail.com").build();
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).fullName("Batch Student").build();

        when(studentService.createBatch(anyList())).thenReturn(List.of(res));

        mockMvc.perform(post(ApiConstants.BASE_STUDENT_URL + "/batch")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(req))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].fullName").value("Batch Student"));
    }

    // ==========================================
    // 6. DYNAMIC FIELD SEARCH & PAGINATION
    // ==========================================

    @Test
    @DisplayName("Get student by dynamic field - 200 OK")
    void getStudentByField_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).phone("0912345678").build();
        when(studentService.getByField("phone", "0912345678")).thenReturn(res);

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/by-field?field=phone&value=0912345678")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("0912345678"));
    }

    @Test
    @DisplayName("Get student list by dynamic field - 200 OK")
    void getStudentsByFieldList_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).address("Hanoi").build();
        when(studentService.findByFieldList("address", "Hanoi")).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/by-field-list?field=address&value=Hanoi")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("Hanoi"));
    }

    @Test
    @DisplayName("Get student page by dynamic field - 200 OK")
    void getStudentsByFieldPaged_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).address("Hanoi").build();
        Page<StudentResponseDTO> page = new PageImpl<>(List.of(res), PageRequest.of(0, 10), 1);

        when(studentService.findByFieldPaged(eq("address"), eq("Hanoi"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/by-field-paged?field=address&value=Hanoi&page=0&size=10")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Search students by keyword - 200 OK")
    void searchStudents_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).fullName("Nguyen Van A").build();
        when(studentService.searchByKeyword("Nguyen")).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/search?keyword=Nguyen")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Nguyen Van A"));
    }

    @Test
    @DisplayName("Search students by specification - 200 OK")
    void searchStudentsBySpecification_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).fullName("Nguyen Van A").build();
        when(studentService.searchBySpecification("fullName:Nguyen")).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/spec-search?search=fullName:Nguyen")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Nguyen Van A"));
    }

    @Test
    @DisplayName("Get all students sorted - 200 OK")
    void getAllStudentsSorted_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).fullName("An").build();
        when(studentService.getAllSorted("fullName", "asc")).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/sorted?sortBy=fullName&direction=asc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("An"));
    }

    @Test
    @DisplayName("Get all students paged - 200 OK")
    void getAllStudentsPaged_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).fullName("Paged Student").build();
        Page<StudentResponseDTO> page = new PageImpl<>(List.of(res), PageRequest.of(0, 10), 1);
        when(studentService.getAllPaged(0, 10, "id", "asc")).thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/paged?page=0&size=10&sortBy=id&direction=asc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Paged Student"));
    }

    @Test
    @DisplayName("Admin gets student count - 200 OK")
    void getStudentCount_Admin_Success() throws Exception {
        when(studentService.getCountMap()).thenReturn(Map.of("status", "SUCCESS", "count", 50));

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/count")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(50));
    }

    @Test
    @DisplayName("Student cannot get student count - 403 Forbidden")
    void getStudentCount_Student_Forbidden() throws Exception {
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/count")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 7. ADDITIONAL ENDPOINTS & EXCEPTION COVERAGE
    // ==========================================

    @Test
    @DisplayName("Search students by spec sorted - 200 OK")
    void searchStudentsBySpecificationSorted_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).fullName("Nguyen Van A").build();
        when(studentService.searchBySpecificationSorted(eq("fullName:Nguyen"), eq("fullName"), eq("asc"))).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/spec-search-sorted?search=fullName:Nguyen&sortBy=fullName&direction=asc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Nguyen Van A"));
    }

    @Test
    @DisplayName("Search students by spec paged - 200 OK")
    void searchStudentsBySpecificationPaged_Success() throws Exception {
        StudentResponseDTO res = StudentResponseDTO.builder().id(1L).fullName("Nguyen Van A").build();
        Page<StudentResponseDTO> page = new PageImpl<>(List.of(res), PageRequest.of(0, 10), 1);
        when(studentService.searchBySpecificationPaged(eq("fullName:Nguyen"), anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/spec-search-paged?search=fullName:Nguyen&page=0&size=10&sortBy=fullName&direction=asc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Nguyen Van A"));
    }

    @Test
    @DisplayName("Exception handling in student create, update, delete, batch endpoints")
    void controllerCatchBlocks_StudentWriteEndpoints() throws Exception {
        StudentRequestDTO req = StudentRequestDTO.builder().fullName("Test").email("test@example.com").build();

        when(studentService.create(any())).thenThrow(new RuntimeException("Create fail"));
        mockMvc.perform(post(ApiConstants.BASE_STUDENT_URL)
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        when(studentService.update(any(), eq(1L))).thenThrow(new RuntimeException("Update fail"));
        mockMvc.perform(put(ApiConstants.BASE_STUDENT_URL + "/1")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        doThrow(new RuntimeException("Delete fail")).when(studentService).delete(1L);
        mockMvc.perform(delete(ApiConstants.BASE_STUDENT_URL + "/1")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isInternalServerError());

        when(studentService.createBatch(any())).thenThrow(new RuntimeException("Batch fail"));
        mockMvc.perform(post(ApiConstants.BASE_STUDENT_URL + "/batch")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(req))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Exception handling in student query, search, count, sorted, paged endpoints")
    void controllerCatchBlocks_StudentReadEndpoints() throws Exception {
        // queryEmail branch and exception
        when(studentService.getByEmail("test@example.com")).thenThrow(new RuntimeException("Email fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/by-email?queryEmail=test@example.com")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.getByField(anyString(), anyString())).thenThrow(new RuntimeException("Field fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/by-field?field=email&value=test@example.com")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.findByFieldList(anyString(), anyString())).thenThrow(new RuntimeException("Field list fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/by-field-list?field=email&value=test@example.com")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.findByFieldPaged(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Field paged fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/by-field-paged?field=email&value=test@example.com")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.searchByKeyword(anyString())).thenThrow(new RuntimeException("Search fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/search?keyword=test")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.searchBySpecification(anyString())).thenThrow(new RuntimeException("Spec fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/spec-search?search=email:test")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.getAllSorted(anyString(), anyString())).thenThrow(new RuntimeException("Sorted fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/sorted")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.getAllPaged(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new RuntimeException("Paged fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/paged")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.searchBySpecificationSorted(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Spec sorted fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/spec-search-sorted?search=email:test")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.searchBySpecificationPaged(anyString(), anyInt(), anyInt(), anyString(), anyString())).thenThrow(new RuntimeException("Spec paged fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/spec-search-paged?search=email:test")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(studentService.getCountMap()).thenThrow(new RuntimeException("Count fail"));
        mockMvc.perform(get(ApiConstants.BASE_STUDENT_URL + "/count")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isInternalServerError());
    }
}
