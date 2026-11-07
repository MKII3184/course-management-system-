package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Student;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = {"StudentRest"})
@SwaggerDefinition(tags = {@Tag(name = "StudentRest", description = "REST APIs for Student module")})
public interface StudentRest {

    @PostMapping(value = {"", "/create"})
    @ApiOperation(value = "create new Student", tags = {"create"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Student> createStudent(@Valid @RequestBody StudentRequestDTO requestDTO);

    @PutMapping(value = {"/{id}", "/update/{id}"})
    @ApiOperation(value = "update existing Student", tags = {"update"}, authorizations = {
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
    ResponseEntity<Student> updateStudent(
            @ApiParam(value = "Student ID", required = true) @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO requestDTO);

    @PostMapping("/update")
    @ApiOperation(value = "update Student by JSON body containing ID and fields", tags = {"update"}, authorizations = {
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
    ResponseEntity<Map<String, String>> updateStudentByMap(@RequestBody Map<String, Object> requestMap);

    @DeleteMapping("/{id}")
    @ApiOperation(value = "delete Student by ID", tags = {"delete"}, authorizations = {
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
    ResponseEntity<Map<String, String>> deleteStudent(@ApiParam(value = "Student ID", required = true) @PathVariable Long id);

    @PostMapping("/delete")
    @ApiOperation(value = "delete Student by JSON body containing ID", tags = {"delete"}, authorizations = {
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
    ResponseEntity<Map<String, String>> deleteStudentByMap(@RequestBody Map<String, String> requestMap);

    @GetMapping("/{id}")
    @ApiOperation(value = "get Student by ID", tags = {"getById"}, authorizations = {
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
    ResponseEntity<StudentResponseDTO> getStudentById(@ApiParam(value = "Student ID", required = true) @PathVariable Long id);

    @GetMapping(value = {"", "/list"})
    @ApiOperation(value = "get all Students", tags = {"list"}, authorizations = {
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
    ResponseEntity<List<StudentResponseDTO>> getAllStudents();

    @GetMapping(value = {"/email/{email}", "/by-email"})
    @ApiOperation(value = "get Student by email", tags = {"getByEmail"}, authorizations = {
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
    ResponseEntity<StudentResponseDTO> getStudentByEmail(
            @ApiParam(value = "Student Email") @PathVariable(required = false) String email,
            @ApiParam(value = "Query Email") @RequestParam(required = false) String queryEmail);

    @PostMapping("/batch")
    @ApiOperation(value = "batch create Students", tags = {"batch"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<StudentResponseDTO>> createStudentsBatch(@RequestBody List<StudentRequestDTO> requestDTOs);

    @GetMapping("/by-field")
    @ApiOperation(value = "get Student by dynamic field", tags = {"getByField"}, authorizations = {
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
    ResponseEntity<StudentResponseDTO> getStudentByField(
            @ApiParam(value = "Field name", required = true) @RequestParam String field,
            @ApiParam(value = "Field value", required = true) @RequestParam String value);

    @GetMapping("/by-field-list")
    @ApiOperation(value = "get Students list by dynamic field", tags = {"getByFieldList"}, authorizations = {
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
    ResponseEntity<List<StudentResponseDTO>> getStudentsByFieldList(
            @ApiParam(value = "Field name", required = true) @RequestParam String field,
            @ApiParam(value = "Field value", required = true) @RequestParam String value);

    @GetMapping("/by-field-paged")
    @ApiOperation(value = "get Students paged by dynamic field", tags = {"getByFieldPaged"}, authorizations = {
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
    ResponseEntity<Page<StudentResponseDTO>> getStudentsByFieldPaged(
            @ApiParam(value = "Field name", required = true) @RequestParam String field,
            @ApiParam(value = "Field value", required = true) @RequestParam String value,
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/search")
    @ApiOperation(value = "search Students by keyword", tags = {"search"}, authorizations = {
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
    ResponseEntity<List<StudentResponseDTO>> searchStudents(@ApiParam(value = "Keyword", required = true) @RequestParam String keyword);

    @GetMapping("/spec-search")
    @ApiOperation(value = "search Students by JPA Specification", tags = {"spec-search"}, authorizations = {
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
    ResponseEntity<List<StudentResponseDTO>> searchStudentsBySpecification(@ApiParam(value = "Search query (e.g. fullName:Nguyen,email:test@)", required = true) @RequestParam String search);

    @GetMapping("/sorted")
    @ApiOperation(value = "get all Students sorted", tags = {"sorted"}, authorizations = {
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
    ResponseEntity<List<StudentResponseDTO>> getAllStudentsSorted(
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/paged")
    @ApiOperation(value = "get Students paged", tags = {"paged"}, authorizations = {
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
    ResponseEntity<Page<StudentResponseDTO>> getAllStudentsPaged(
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/spec-search-sorted")
    @ApiOperation(value = "search Students by specification sorted", tags = {"spec-search-sorted"}, authorizations = {
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
    ResponseEntity<List<StudentResponseDTO>> searchStudentsBySpecificationSorted(
            @ApiParam(value = "Search query", required = true) @RequestParam String search,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/spec-search-paged")
    @ApiOperation(value = "search Students by specification paged", tags = {"spec-search-paged"}, authorizations = {
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
    ResponseEntity<Page<StudentResponseDTO>> searchStudentsBySpecificationPaged(
            @ApiParam(value = "Search query", required = true) @RequestParam String search,
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "asc") String direction);

    @GetMapping("/count")
    @ApiOperation(value = "get total Student count", tags = {"count"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Map<String, Object>> getStudentCount();
}
