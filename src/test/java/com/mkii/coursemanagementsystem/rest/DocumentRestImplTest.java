package com.mkii.coursemanagementsystem.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.entity.Document;
import com.mkii.coursemanagementsystem.service.DocumentService;
import com.mkii.coursemanagementsystem.service.MinioStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentRestImplTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private MinioStorageService minioStorageService;

    // ==========================================
    // 1. UPLOAD / CREATE DOCUMENT
    // ==========================================

    @Test
    @DisplayName("Admin creates document with file upload - 200 OK")
    void createDocument_Admin_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "sample.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "dummy pdf content".getBytes()
        );

        Document doc = Document.builder()
                .id("doc-uuid-1")
                .name("sample.pdf")
                .appId("APP001")
                .version(1.0)
                .deleted(false)
                .active(true)
                .creationTime(new Date())
                .build();

        when(documentService.create(anyList(), eq("APP001"))).thenReturn(List.of(doc));

        mockMvc.perform(multipart(ApiConstants.BASE_DOCUMENT_URL + "/create")
                        .file(file)
                        .param("appId", "APP001")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("doc-uuid-1"))
                .andExpect(jsonPath("$[0].name").value("sample.pdf"));
    }

    @Test
    @DisplayName("Student cannot upload document - 403 Forbidden")
    void createDocument_Student_Forbidden() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "test".getBytes()
        );

        mockMvc.perform(multipart(ApiConstants.BASE_DOCUMENT_URL + "/create")
                        .file(file)
                        .param("appId", "APP001")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 2. DOWNLOAD DOCUMENT
    // ==========================================

    @Test
    @DisplayName("Download document by ID - 200 OK")
    void downloadDocumentById_Success() throws Exception {
        byte[] content = "file-content-bytes".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"doc.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);

        when(documentService.downloadById("doc-123")).thenReturn(responseEntity);

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/download/doc-123")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(content().bytes(content));
    }

    @Test
    @DisplayName("Download document by file path - 200 OK")
    void downloadDocumentByPath_Success() throws Exception {
        byte[] content = "path-file-bytes".getBytes();
        ResponseEntity<byte[]> responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"path_doc.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);

        when(documentService.download("documents/sample.pdf")).thenReturn(responseEntity);

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/download")
                        .param("filePath", "documents/sample.pdf")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(content().bytes(content));
    }

    // ==========================================
    // 3. DELETE DOCUMENT
    // ==========================================

    @Test
    @DisplayName("Admin deletes document by Path ID - 200 OK")
    void deleteDocumentById_Admin_Success() throws Exception {
        when(documentService.deleteById("doc-123")).thenReturn(Map.of(MessageConstants.STATUS, MessageConstants.SUCCESS, MessageConstants.MESSAGE, MessageConstants.DOCUMENT_DELETED_SUCCESS));

        mockMvc.perform(delete(ApiConstants.BASE_DOCUMENT_URL + "/doc-123")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(MessageConstants.SUCCESS));
    }

    @Test
    @DisplayName("Student cannot delete document by Path ID - 403 Forbidden")
    void deleteDocumentById_Student_Forbidden() throws Exception {
        mockMvc.perform(delete(ApiConstants.BASE_DOCUMENT_URL + "/doc-123")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin deletes document by JSON Body - 200 OK")
    void deleteDocument_JsonBody_Admin_Success() throws Exception {
        Map<String, String> body = Map.of("id", "doc-456");
        when(documentService.delete(body)).thenReturn(Map.of(MessageConstants.STATUS, MessageConstants.SUCCESS, MessageConstants.MESSAGE, MessageConstants.DOCUMENT_DELETED_SUCCESS));

        mockMvc.perform(post(ApiConstants.BASE_DOCUMENT_URL + "/delete")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(MessageConstants.SUCCESS));
    }

    @Test
    @DisplayName("Student cannot delete document by JSON Body - 403 Forbidden")
    void deleteDocument_JsonBody_Student_Forbidden() throws Exception {
        Map<String, String> body = Map.of("id", "doc-456");

        mockMvc.perform(post(ApiConstants.BASE_DOCUMENT_URL + "/delete")
                        .with(httpBasic("student", "student123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 4. GET DOCUMENT DETAILS & APP ID
    // ==========================================

    @Test
    @DisplayName("Get document by ID - 200 OK")
    void getDocumentById_Success() throws Exception {
        Document doc = Document.builder().id("doc-1").name("file1.pdf").build();
        when(documentService.getById("doc-1")).thenReturn(doc);

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/doc-1")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("doc-1"))
                .andExpect(jsonPath("$.name").value("file1.pdf"));
    }

    @Test
    @DisplayName("Get documents by appId - 200 OK")
    void getDocumentsByAppId_Success() throws Exception {
        Document doc = Document.builder().id("doc-1").appId("APP999").name("file1.pdf").build();
        when(documentService.getByAppId("APP999")).thenReturn(List.of(doc));

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/app/APP999")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appId").value("APP999"));
    }

    @Test
    @DisplayName("Get documents by appId paged - 200 OK")
    void getDocumentsByAppIdPaged_Success() throws Exception {
        Document doc = Document.builder().id("doc-1").appId("APP999").build();
        Page<Document> page = new PageImpl<>(List.of(doc), PageRequest.of(0, 10), 1);
        when(documentService.getByAppIdPaged(eq("APP999"), anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/app/APP999/paged?page=0&size=10")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // ==========================================
    // 5. LIST, PAGED, SORTED, SEARCH & COUNT
    // ==========================================

    @Test
    @DisplayName("Get all active documents - 200 OK")
    void getAllActiveDocuments_Success() throws Exception {
        Document doc = Document.builder().id("doc-1").name("active.pdf").build();
        when(documentService.getAllActive()).thenReturn(List.of(doc));

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/list")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("active.pdf"));
    }

    @Test
    @DisplayName("Get documents paged - 200 OK")
    void getDocumentsPaged_Success() throws Exception {
        Document doc = Document.builder().id("doc-1").build();
        Page<Document> page = new PageImpl<>(List.of(doc), PageRequest.of(0, 10), 1);
        when(documentService.getAllPaged(anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/paged?page=0&size=10")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Get documents sorted - 200 OK")
    void getDocumentsSorted_Success() throws Exception {
        Document doc = Document.builder().id("doc-1").name("sorted.pdf").build();
        when(documentService.getAllSorted("name", "asc")).thenReturn(List.of(doc));

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/sorted?sortBy=name&direction=asc")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("sorted.pdf"));
    }

    @Test
    @DisplayName("Search documents - 200 OK")
    void searchDocuments_Success() throws Exception {
        Document doc = Document.builder().id("doc-1").name("search.pdf").build();
        when(documentService.getDoc(anyString(), anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(doc));

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/search")
                        .param("query", "search")
                        .param("appId", "APP001")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("search.pdf"));
    }

    @Test
    @DisplayName("Admin gets document count - 200 OK")
    void getDocCount_Admin_Success() throws Exception {
        when(documentService.getDocCountMap(any(), any())).thenReturn(Map.of("status", "SUCCESS", "count", 42));

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/count")
                        .param("query", "test")
                        .param("appId", "APP001")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(42));
    }

    @Test
    @DisplayName("Student cannot get document count - 403 Forbidden")
    void getDocCount_Student_Forbidden() throws Exception {
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/count")
                        .param("query", "test")
                        .param("appId", "APP001")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Search documents by specification paged - 200 OK")
    void searchBySpecificationPaged_Success() throws Exception {
        Document doc = Document.builder().id("doc-1").name("spec.pdf").build();
        Page<Document> page = new PageImpl<>(List.of(doc), PageRequest.of(0, 10), 1);
        when(documentService.searchBySpecificationPaged(eq("name:spec"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/spec-search-paged")
                        .param("search", "name:spec")
                        .param("page", "0")
                        .param("size", "10")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("spec.pdf"));
    }

    @Test
    @DisplayName("Exception handling in document write endpoints (upload, delete)")
    void controllerCatchBlocks_DocumentWriteEndpoints() throws Exception {
        org.springframework.mock.web.MockMultipartFile file =
                new org.springframework.mock.web.MockMultipartFile("files", "test.pdf", "application/pdf", "content".getBytes());

        when(documentService.create(any(), any())).thenThrow(new RuntimeException("Upload fail"));
        mockMvc.perform(multipart(ApiConstants.BASE_DOCUMENT_URL)
                        .file(file)
                        .param("appId", "APP001")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isInternalServerError());

        when(documentService.delete(any())).thenThrow(new RuntimeException("Delete fail"));
        mockMvc.perform(post(ApiConstants.BASE_DOCUMENT_URL + "/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"doc-1\"}")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isInternalServerError());

        when(documentService.deleteById(anyString())).thenThrow(new RuntimeException("DeleteById fail"));
        mockMvc.perform(delete(ApiConstants.BASE_DOCUMENT_URL + "/doc-1")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Exception handling in document read and download endpoints")
    void controllerCatchBlocks_DocumentReadEndpoints() throws Exception {
        when(documentService.download(anyString())).thenThrow(new RuntimeException("Download fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/download")
                        .param("filePath", "test/path.pdf")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(documentService.downloadById(anyString())).thenThrow(new RuntimeException("DownloadById fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/download/doc-1")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(documentService.getById(anyString())).thenThrow(new RuntimeException("GetById fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/doc-1")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(documentService.getByAppId(anyString())).thenThrow(new RuntimeException("GetByAppId fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/app/APP001")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(documentService.getByAppIdPaged(anyString(), anyInt(), anyInt(), anyString(), anyString())).thenThrow(new RuntimeException("AppIdPaged fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/app/APP001/paged")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(documentService.getAllPaged(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new RuntimeException("Paged fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/paged")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(documentService.getAllSorted(anyString(), anyString())).thenThrow(new RuntimeException("Sorted fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/sorted")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(documentService.getDoc(any(), any(), any(), any(), any(), any())).thenThrow(new RuntimeException("Search fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/search")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());

        when(documentService.getDocCountMap(any(), any())).thenThrow(new RuntimeException("Count fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/count")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isInternalServerError());

        when(documentService.searchBySpecificationPaged(any(), anyInt(), anyInt(), any(), any())).thenThrow(new RuntimeException("Spec fail"));
        mockMvc.perform(get(ApiConstants.BASE_DOCUMENT_URL + "/spec-search-paged")
                        .with(httpBasic("student", "student123")))
                .andExpect(status().isInternalServerError());
    }
}
