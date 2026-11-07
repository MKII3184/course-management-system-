package com.mkii.coursemanagementsystem.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.entity.Course;
import com.mkii.coursemanagementsystem.service.CourseService;
import com.mkii.coursemanagementsystem.service.MinioStorageService;
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
public class CourseRestImplTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private MinioStorageService minioStorageService;

    // ==========================================
    // 1. CREATE COURSE
    // ==========================================

    @Test
    @DisplayName("Admin creates course successfully - 201 Created")
    void createCourse_Admin_Success() throws Exception {
        CourseRequestDTO requestDTO = CourseRequestDTO.builder()
                .title("Java Core Master")
                .description("Complete Java Course")
                .duration(60)
                .price(199.99)
                .build();

        Course mockCourse = Course.builder()
                .id(1L)
                .title("Java Core Master")
                .description("Complete Java Course")
                .duration(60)
                .price(199.99)
                .build();

        when(courseService.create(any(CourseRequestDTO.class))).thenReturn(mockCourse);

        mockMvc.perform(post(ApiConstants.BASE_COURSE_URL + "/create")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Java Core Master"));
    }

    @Test
    @DisplayName("Lecturer creates course successfully - 201 Created")
    void createCourse_Lecturer_Success() throws Exception {
        CourseRequestDTO requestDTO = CourseRequestDTO.builder()
                .title("Spring Boot Advanced")
                .description("Microservices")
                .duration(45)
                .price(149.0)
                .build();

        Course mockCourse = Course.builder()
                .id(2L)
                .title("Spring Boot Advanced")
                .description("Microservices")
                .duration(45)
                .price(149.0)
                .build();

        when(courseService.create(any(CourseRequestDTO.class))).thenReturn(mockCourse);

        mockMvc.perform(post(ApiConstants.BASE_COURSE_URL)
                        .with(httpBasic("lecturer", "lecturer123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @DisplayName("Student cannot create course - 403 Forbidden")
    void createCourse_Student_Forbidden() throws Exception {
        CourseRequestDTO requestDTO = CourseRequestDTO.builder()
                .title("Unauthorized Course")
                .duration(10)
                .price(50.0)
                .build();

        mockMvc.perform(post(ApiConstants.BASE_COURSE_URL + "/create")
                        .with(httpBasic("student", "student123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Create course validation error - 400 Bad Request")
    void createCourse_ValidationError_BadRequest() throws Exception {
        CourseRequestDTO invalidDTO = CourseRequestDTO.builder()
                .title("") // Blank title violates @NotBlank
                .duration(-5) // Negative duration violates @PositiveOrZero
                .price(-10.0)
                .build();

        mockMvc.perform(post(ApiConstants.BASE_COURSE_URL + "/create")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // 2. UPDATE COURSE
    // ==========================================

    @Test
    @DisplayName("Admin updates course successfully - 200 OK")
    void updateCourse_Admin_Success() throws Exception {
        CourseRequestDTO requestDTO = CourseRequestDTO.builder()
                .title("Updated Title")
                .duration(40)
                .price(80.0)
                .build();

        Course updatedCourse = Course.builder()
                .id(1L)
                .title("Updated Title")
                .duration(40)
                .price(80.0)
                .build();

        when(courseService.update(any(CourseRequestDTO.class), eq(1L))).thenReturn(updatedCourse);

        mockMvc.perform(put(ApiConstants.BASE_COURSE_URL + "/1")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("Student cannot update course - 403 Forbidden")
    void updateCourse_Student_Forbidden() throws Exception {
        CourseRequestDTO requestDTO = CourseRequestDTO.builder()
                .title("Update Course")
                .duration(10)
                .price(50.0)
                .build();

        mockMvc.perform(put(ApiConstants.BASE_COURSE_URL + "/update/1")
                        .with(httpBasic("student", "student123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 3. DELETE COURSE
    // ==========================================

    @Test
    @DisplayName("Admin deletes course successfully - 200 OK")
    void deleteCourse_Admin_Success() throws Exception {
        when(courseService.delete(1L)).thenReturn(Map.of(MessageConstants.STATUS, MessageConstants.SUCCESS, MessageConstants.MESSAGE, MessageConstants.COURSE_DELETED_SUCCESS));

        mockMvc.perform(delete(ApiConstants.BASE_COURSE_URL + "/1")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(MessageConstants.SUCCESS));
    }

    @Test
    @DisplayName("Lecturer cannot delete course - 403 Forbidden")
    void deleteCourse_Lecturer_Forbidden() throws Exception {
        mockMvc.perform(delete(ApiConstants.BASE_COURSE_URL + "/1")
                        .with(httpBasic("lecturer", "lecturer123")))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 4. GET BY ID & NOT FOUND
    // ==========================================

    @Test
    @DisplayName("Get course by ID - 200 OK")
    void getCourseById_Success() throws Exception {
        CourseResponseDTO responseDTO = CourseResponseDTO.builder()
                .id(1L)
                .title("Java Core")
                .duration(30)
                .price(100.0)
                .build();

        when(courseService.getById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/1")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Java Core"));
    }

    @Test
    @DisplayName("Get course by non-existing ID - 404 Not Found")
    void getCourseById_NotFound() throws Exception {
        when(courseService.getById(999L)).thenThrow(new ResourceNotFoundException("Course not found with id: 999"));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/999")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found with id: 999"));
    }

    // ==========================================
    // 5. GET ALL & BATCH CREATE
    // ==========================================

    @Test
    @DisplayName("Get all courses - 200 OK")
    void getAllCourses_Success() throws Exception {
        CourseResponseDTO c1 = CourseResponseDTO.builder().id(1L).title("Course 1").build();
        when(courseService.getAll()).thenReturn(List.of(c1));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL)
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Course 1"));
    }

    @Test
    @DisplayName("Get course by title - 200 OK")
    void getCourseByTitle_Success() throws Exception {
        CourseResponseDTO c = CourseResponseDTO.builder().id(1L).title("Python").build();
        when(courseService.getByTitle("Python")).thenReturn(c);

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/by-title?queryTitle=Python")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Python"));
    }

    @Test
    @DisplayName("Batch create courses - 201 Created")
    void createCoursesBatch_Admin_Success() throws Exception {
        CourseRequestDTO req = CourseRequestDTO.builder().title("Batch Course").duration(20).price(50.0).build();
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).title("Batch Course").build();

        when(courseService.createBatch(anyList())).thenReturn(List.of(res));

        mockMvc.perform(post(ApiConstants.BASE_COURSE_URL + "/batch")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(req))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].title").value("Batch Course"));
    }

    // ==========================================
    // 6. DYNAMIC FIELD & FILTER
    // ==========================================

    @Test
    @DisplayName("Get course by dynamic field - 200 OK")
    void getCourseByField_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).title("Ruby").build();
        when(courseService.getByField("title", "Ruby")).thenReturn(res);

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/by-field?field=title&value=Ruby")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Ruby"));
    }

    @Test
    @DisplayName("Get course list by dynamic field - 200 OK")
    void getCoursesByFieldList_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).duration(30).build();
        when(courseService.findByFieldList("duration", "30")).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/by-field-list?field=duration&value=30")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].duration").value(30));
    }

    @Test
    @DisplayName("Get course page by dynamic field - 200 OK")
    void getCoursesByFieldPaged_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).duration(30).build();
        Page<CourseResponseDTO> page = new PageImpl<>(List.of(res), PageRequest.of(0, 10), 1);

        when(courseService.findByFieldPaged(eq("duration"), eq("30"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/by-field-paged?field=duration&value=30&page=0&size=10")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Filter courses by price range - 200 OK")
    void filterCoursesByPrice_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).price(150.0).build();
        when(courseService.filterByPrice(100.0, 200.0)).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/filter?minPrice=100.0&maxPrice=200.0")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(150.0));
    }

    // ==========================================
    // 7. SPECIFICATION & PAGINATION & COUNT
    // ==========================================

    @Test
    @DisplayName("Search courses by specification - 200 OK")
    void searchCoursesBySpecification_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).title("Java Pro").build();
        when(courseService.searchBySpecification("title:Java")).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/spec-search?search=title:Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Pro"));
    }

    @Test
    @DisplayName("Get all courses sorted - 200 OK")
    void getAllCoursesSorted_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).title("A Course").build();
        when(courseService.getAllSorted("title", "asc")).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/sorted?sortBy=title&direction=asc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("A Course"));
    }

    @Test
    @DisplayName("Get all courses paged - 200 OK")
    void getAllCoursesPaged_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).title("Paged Course").build();
        Page<CourseResponseDTO> page = new PageImpl<>(List.of(res), PageRequest.of(0, 10), 1);
        when(courseService.getAllPaged(0, 10, "id", "asc")).thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/paged?page=0&size=10&sortBy=id&direction=asc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Paged Course"));
    }

    @Test
    @DisplayName("Admin gets course count - 200 OK")
    void getCourseCount_Admin_Success() throws Exception {
        when(courseService.getCountMap()).thenReturn(Map.of("status", "SUCCESS", "count", 25));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/count")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(25));
    }

    @Test
    @DisplayName("Student cannot get course count - 403 Forbidden")
    void getCourseCount_Student_Forbidden() throws Exception {
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/count")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 8. ADDITIONAL ENDPOINTS & EXCEPTION COVERAGE
    // ==========================================

    @Test
    @DisplayName("Search courses by keyword - 200 OK")
    void searchCourses_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).title("Java Pro").build();
        when(courseService.searchByTitle("Java")).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/search?keyword=Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Pro"));
    }

    @Test
    @DisplayName("Search courses by spec sorted - 200 OK")
    void searchCoursesBySpecificationSorted_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).title("Java Pro").build();
        when(courseService.searchBySpecificationSorted(eq("title:Java"), eq("price"), eq("desc"))).thenReturn(List.of(res));

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/spec-search-sorted?search=title:Java&sortBy=price&direction=desc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Pro"));
    }

    @Test
    @DisplayName("Search courses by spec paged - 200 OK")
    void searchCoursesBySpecificationPaged_Success() throws Exception {
        CourseResponseDTO res = CourseResponseDTO.builder().id(1L).title("Java Pro").build();
        Page<CourseResponseDTO> page = new PageImpl<>(List.of(res), PageRequest.of(0, 10), 1);
        when(courseService.searchBySpecificationPaged(eq("title:Java"), anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/spec-search-paged?search=title:Java&page=0&size=10&sortBy=price&direction=desc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Java Pro"));
    }

    @Test
    @DisplayName("Exception handling in create, update, delete, batch endpoints")
    void controllerCatchBlocks_WriteEndpoints() throws Exception {
        CourseRequestDTO req = CourseRequestDTO.builder().title("Java").duration(10).price(10.0).build();

        when(courseService.create(any())).thenThrow(new RuntimeException("Create fail"));
        mockMvc.perform(post(ApiConstants.BASE_COURSE_URL + "/create")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        when(courseService.update(any(), eq(1L))).thenThrow(new RuntimeException("Update fail"));
        mockMvc.perform(put(ApiConstants.BASE_COURSE_URL + "/1")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        doThrow(new RuntimeException("Delete fail")).when(courseService).delete(1L);
        mockMvc.perform(delete(ApiConstants.BASE_COURSE_URL + "/1")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isInternalServerError());

        when(courseService.createBatch(any())).thenThrow(new RuntimeException("Batch fail"));
        mockMvc.perform(post(ApiConstants.BASE_COURSE_URL + "/batch")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(req))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Exception handling in query, filter, search, count, sorted, paged endpoints")
    void controllerCatchBlocks_ReadEndpoints() throws Exception {
        // queryTitle branch and exception
        when(courseService.getByTitle("Java")).thenThrow(new RuntimeException("Title fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/by-title?queryTitle=Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.getByField(anyString(), anyString())).thenThrow(new RuntimeException("Field fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/by-field?field=title&value=Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.findByFieldList(anyString(), anyString())).thenThrow(new RuntimeException("Field list fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/by-field-list?field=title&value=Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.findByFieldPaged(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Field paged fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/by-field-paged?field=title&value=Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.filterByPrice(anyDouble(), anyDouble())).thenThrow(new RuntimeException("Filter fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/filter/price?min=10&max=100")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.searchByTitle(anyString())).thenThrow(new RuntimeException("Search fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/search?keyword=Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.searchBySpecification(anyString())).thenThrow(new RuntimeException("Spec fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/spec-search?search=title:Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.getAllSorted(anyString(), anyString())).thenThrow(new RuntimeException("Sorted fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/sorted")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.getAllPaged(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new RuntimeException("Paged fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/paged")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.searchBySpecificationSorted(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("Spec sorted fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/spec-search-sorted?search=title:Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.searchBySpecificationPaged(anyString(), anyInt(), anyInt(), anyString(), anyString())).thenThrow(new RuntimeException("Spec paged fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/spec-search-paged?search=title:Java")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(courseService.getCountMap()).thenThrow(new RuntimeException("Count fail"));
        mockMvc.perform(get(ApiConstants.BASE_COURSE_URL + "/count")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isInternalServerError());
    }
}
