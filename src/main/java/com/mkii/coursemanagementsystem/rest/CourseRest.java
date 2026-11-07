package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.entity.Course;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = {"CourseRest"})
@SwaggerDefinition(tags = {@Tag(name = "CourseRest", description = "REST APIs for Course module")})
public interface CourseRest {

    @PostMapping(value = {"", "/create"})
    @ApiOperation(value = "create new Course", tags = {"create"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequestDTO requestDTO);

    @PutMapping(value = {"/{id}", "/update/{id}"})
    @ApiOperation(value = "update existing Course", tags = {"update"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied"),
            @ApiResponse(code = 404, message = "Not Found!!!")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ResponseEntity<Course> updateCourse(
            @ApiParam(value = "Course ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO requestDTO);

    @PostMapping("/update")
    @ApiOperation(value = "update Course by JSON body containing ID and fields", tags = {"update"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Access is denied"),
            @ApiResponse(code = 404, message = "Not Found!!!")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ResponseEntity<Map<String, String>> updateCourseByMap(@RequestBody Map<String, Object> requestMap);

    @DeleteMapping("/{id}")
    @ApiOperation(value = "delete Course by ID", tags = {"delete"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied"),
            @ApiResponse(code = 404, message = "Not Found!!!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Map<String, String>> deleteCourse(@ApiParam(value = "Course ID", required = true) @PathVariable Long id);

    @PostMapping("/delete")
    @ApiOperation(value = "delete Course by JSON body containing ID", tags = {"delete"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Access is denied"),
            @ApiResponse(code = 404, message = "Not Found!!!")
    })
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Map<String, String>> deleteCourseByMap(@RequestBody Map<String, String> requestMap);

    @GetMapping("/{id}")
    @ApiOperation(value = "get Course by ID", tags = {"getById"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied"),
            @ApiResponse(code = 404, message = "Not Found!!!")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<CourseResponseDTO> getCourseById(@ApiParam(value = "Course ID", required = true) @PathVariable Long id);

    @GetMapping(value = {"", "/list"})
    @ApiOperation(value = "get all Courses", tags = {"list"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK", response = List.class),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<List<CourseResponseDTO>> getAllCourses();

    @GetMapping(value = {"/title/{title}", "/by-title"})
    @ApiOperation(value = "get Course by Title", tags = {"getByTitle"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied"),
            @ApiResponse(code = 404, message = "Not Found!!!")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<CourseResponseDTO> getCourseByTitle(
            @ApiParam(value = "Course Title") @PathVariable(required = false) String title,
            @ApiParam(value = "Query Title") @RequestParam(required = false) String queryTitle);

    @PostMapping("/batch")
    @ApiOperation(value = "batch create Courses", tags = {"batch"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<CourseResponseDTO>> createCoursesBatch(@RequestBody List<CourseRequestDTO> requestDTOs);

    @GetMapping("/by-field")
    @ApiOperation(value = "get Course by dynamic field", tags = {"getByField"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied"),
            @ApiResponse(code = 404, message = "Not Found!!!")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<CourseResponseDTO> getCourseByField(
            @ApiParam(value = "Field name", required = true) @RequestParam String field,
            @ApiParam(value = "Field value", required = true) @RequestParam String value);

    @GetMapping("/by-field-list")
    @ApiOperation(value = "get Courses list by dynamic field", tags = {"getByFieldList"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK", response = List.class),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<List<CourseResponseDTO>> getCoursesByFieldList(
            @ApiParam(value = "Field name", required = true) @RequestParam String field,
            @ApiParam(value = "Field value", required = true) @RequestParam String value);

    @GetMapping("/by-field-paged")
    @ApiOperation(value = "get Courses paged by dynamic field", tags = {"getByFieldPaged"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<Page<CourseResponseDTO>> getCoursesByFieldPaged(
            @ApiParam(value = "Field name", required = true) @RequestParam String field,
            @ApiParam(value = "Field value", required = true) @RequestParam String value,
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/search")
    @ApiOperation(value = "search Courses by keyword", tags = {"search"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK", response = List.class),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<List<CourseResponseDTO>> searchCourses(@ApiParam(value = "Keyword", required = true) @RequestParam String keyword);

    @GetMapping("/filter")
    @ApiOperation(value = "filter Courses by price range", tags = {"filter"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK", response = List.class),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<List<CourseResponseDTO>> filterCoursesByPrice(
            @ApiParam(value = "Minimum price", required = true) @RequestParam Double minPrice,
            @ApiParam(value = "Maximum price", required = true) @RequestParam Double maxPrice);

    @GetMapping("/spec-search")
    @ApiOperation(value = "search Courses by JPA Specification", tags = {"spec-search"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK", response = List.class),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<List<CourseResponseDTO>> searchCoursesBySpecification(@ApiParam(value = "Search query (e.g. title:Java,price>100)", required = true) @RequestParam String search);

    @GetMapping("/sorted")
    @ApiOperation(value = "get all Courses sorted", tags = {"sorted"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK", response = List.class),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<List<CourseResponseDTO>> getAllCoursesSorted(
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/paged")
    @ApiOperation(value = "get Courses paged", tags = {"paged"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<Page<CourseResponseDTO>> getAllCoursesPaged(
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/spec-search-sorted")
    @ApiOperation(value = "search Courses by specification sorted", tags = {"spec-search-sorted"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK", response = List.class),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<List<CourseResponseDTO>> searchCoursesBySpecificationSorted(
            @ApiParam(value = "Search query", required = true) @RequestParam String search,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/spec-search-paged")
    @ApiOperation(value = "search Courses by specification paged", tags = {"spec-search-paged"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_STUDENT", description = "Student role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role"),
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('STUDENT') or hasRole('LECTURER') or hasRole('ADMIN')")
    ResponseEntity<Page<CourseResponseDTO>> searchCoursesBySpecificationPaged(
            @ApiParam(value = "Search query", required = true) @RequestParam String search,
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/count")
    @ApiOperation(value = "get total Course count", tags = {"count"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ResponseEntity<Map<String, Object>> getCourseCount();
}
