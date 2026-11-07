package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.entity.Document;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Api(tags = {"DocumentRest"})
@SwaggerDefinition(tags = {@Tag(name = "DocumentRest", description = "REST APIs for Document MinIO module")})
public interface DocumentRest {

    @PostMapping(value = {"/create", "/upload", ""}, consumes = "multipart/form-data")
    @ApiOperation(value = "create/upload Document", tags = {"create"}, authorizations = {
            @Authorization(value = "default", scopes = {
                    @AuthorizationScope(scope = "ROLE_ADMIN", description = "Admin role"),
                    @AuthorizationScope(scope = "ROLE_LECTURER", description = "Lecturer role")
            })
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Access is denied")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ResponseEntity<List<Document>> createDocument(
            @ApiParam(value = "Multipart files list", required = true) @RequestParam("files") List<MultipartFile> files,
            @ApiParam(value = "Application workflow ID (APP_DETAIL_FK)", required = true) @RequestParam("appId") String appId);

    @GetMapping("/download")
    @ApiOperation(value = "download Document by file path", tags = {"download"}, authorizations = {
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
    ResponseEntity<byte[]> downloadDocumentByPath(@ApiParam(value = "File path on MinIO", required = true) @RequestParam("filePath") String filePath);

    @GetMapping("/download/{id}")
    @ApiOperation(value = "download Document by ID", tags = {"download"}, authorizations = {
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
    ResponseEntity<byte[]> downloadDocumentById(@ApiParam(value = "Document ID", required = true) @PathVariable String id);

    @PostMapping("/delete")
    @ApiOperation(value = "soft delete Document by JSON body", tags = {"delete"}, authorizations = {
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
    ResponseEntity<Map<String, String>> deleteDocument(@ApiParam(value = "Map with 'id' key", required = true) @RequestBody Map<String, String> id);

    @DeleteMapping("/{id}")
    @ApiOperation(value = "soft delete Document by Path ID", tags = {"delete"}, authorizations = {
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
    ResponseEntity<Map<String, String>> deleteDocumentById(@ApiParam(value = "Document ID", required = true) @PathVariable String id);

    @PostMapping("/restore")
    @ApiOperation(value = "restore soft deleted Document by JSON body", tags = {"restore"}, authorizations = {
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
    ResponseEntity<Map<String, String>> restoreDocument(@ApiParam(value = "Map with 'id' key", required = true) @RequestBody Map<String, String> id);

    @GetMapping("/{id}")
    @ApiOperation(value = "get Document details by ID", tags = {"getById"}, authorizations = {
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
    ResponseEntity<Document> getDocumentById(@ApiParam(value = "Document ID", required = true) @PathVariable String id);

    @GetMapping("/app/{appId}")
    @ApiOperation(value = "get Documents by Application ID (APP_DETAIL_FK)", tags = {"getByAppId"}, authorizations = {
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
    ResponseEntity<List<Document>> getDocumentsByAppId(@ApiParam(value = "Application ID", required = true) @PathVariable String appId);

    @GetMapping("/app/{appId}/paged")
    @ApiOperation(value = "get Documents paged by Application ID", tags = {"getByAppIdPaged"}, authorizations = {
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
    ResponseEntity<Page<Document>> getDocumentsByAppIdPaged(
            @ApiParam(value = "Application ID", required = true) @PathVariable String appId,
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "creationTime") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "desc") String direction);

    @GetMapping(value = {"/list", ""})
    @ApiOperation(value = "get all active Documents", tags = {"list"}, authorizations = {
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
    ResponseEntity<List<Document>> getAllActiveDocuments();

    @GetMapping("/paged")
    @ApiOperation(value = "get Documents paged", tags = {"paged"}, authorizations = {
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
    ResponseEntity<Page<Document>> getDocumentsPaged(
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "creationTime") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "desc") String direction);

    @GetMapping("/sorted")
    @ApiOperation(value = "get Documents sorted", tags = {"sorted"}, authorizations = {
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
    ResponseEntity<List<Document>> getDocumentsSorted(
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "creationTime") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "desc") String direction);

    @GetMapping("/search")
    @ApiOperation(value = "search Documents with pagination and filters", tags = {"search"}, authorizations = {
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
    ResponseEntity<List<Document>> searchDocuments(
            @ApiParam(value = "Query string filter") @RequestParam(required = false) String query,
            @ApiParam(value = "Lower limit offset") @RequestParam(required = false, defaultValue = "0") Integer lowerLimit,
            @ApiParam(value = "Upper limit page size") @RequestParam(required = false, defaultValue = "10") Integer upperLimit,
            @ApiParam(value = "Order by field") @RequestParam(required = false, defaultValue = "creationTime") String orderBy,
            @ApiParam(value = "Order type (asc/desc)") @RequestParam(required = false, defaultValue = "desc") String orderType,
            @ApiParam(value = "Application ID filter") @RequestParam(required = false) String appId);

    @GetMapping("/count")
    @ApiOperation(value = "get Documents count", tags = {"count"}, authorizations = {
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
    ResponseEntity<Map<String, Object>> getDocCount(
            @ApiParam(value = "Query string filter") @RequestParam(required = false) String query,
            @ApiParam(value = "Application ID filter") @RequestParam(required = false) String appId);

    @GetMapping("/spec-search-paged")
    @ApiOperation(value = "search Documents by specification paged", tags = {"spec-search-paged"}, authorizations = {
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
    ResponseEntity<Page<Document>> searchBySpecificationPaged(
            @ApiParam(value = "Search query (e.g. name:test,deleted:false)", required = true) @RequestParam String search,
            @ApiParam(value = "Page number") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "Page size") @RequestParam(defaultValue = "10") int size,
            @ApiParam(value = "Sort field") @RequestParam(defaultValue = "creationTime") String sortBy,
            @ApiParam(value = "Sort direction") @RequestParam(defaultValue = "desc") String direction);
}
