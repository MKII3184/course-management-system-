package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.common.exception.BadRequestException;
import com.mkii.coursemanagementsystem.entity.Document;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentServiceImplIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImplIntegrationTest.class);

    @Autowired
    private DocumentService documentService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private MinioStorageService minioStorageService;

    private static final String OBJECT_INFO_ID = "10e6123d-ef43-4159-8e4c-40a7644d1d64";
    private static final String TEST_FILE_NAME = "cdr_" + (System.currentTimeMillis() % 1000000) + ".pdf";
    private static String createdDocumentId;

    // ===================== CREATE =====================

    @Test
    @Order(1)
    public void createTest() {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                TEST_FILE_NAME,
                "application/pdf",
                "Hoc phan nay giang day ve lap trinh huong doi tuong".getBytes()
        );
        logger.info("MultipartFile: name={}, size={}", file.getOriginalFilename(), file.getSize());

        List<Document> response = documentService.create(List.of(file), OBJECT_INFO_ID);
        logger.info("Response: {}", response);

        assertNotNull(response);
        assertFalse(response.isEmpty());
        Document doc = response.get(0);
        assertNotNull(doc.getId());
        assertEquals(TEST_FILE_NAME, doc.getName());
        assertEquals(OBJECT_INFO_ID, doc.getAppId());
        assertEquals(1.0, doc.getVersion());
        assertTrue(doc.getActive());
        assertFalse(doc.getDeleted());

        createdDocumentId = doc.getId();
    }

    @Test
    @Order(2)
    void create_WithEvaluationCode_Success() {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                TEST_FILE_NAME,
                "application/pdf",
                "Noi dung CDR3: ap dung kien thuc khoa hoc tu nhien va lap trinh".getBytes()
        );

        List<Document> response = documentService.create(List.of(file), OBJECT_INFO_ID);
        assertNotNull(response);
        assertFalse(response.isEmpty());
        Document doc = response.get(0);
        assertNotNull(doc.getId());
        // Verify auto-versioning increment to 2.0
        assertEquals(2.0, doc.getVersion());
        assertTrue(doc.getActive());
        logger.info("Created new version: id={}, version={}", doc.getId(), doc.getVersion());
    }

    @Test
    @Order(3)
    void create_NullWrapper_ThrowsException() {
        assertThrows(BadRequestException.class, () -> documentService.create(null, OBJECT_INFO_ID));
        assertThrows(BadRequestException.class, () -> documentService.create(Collections.emptyList(), OBJECT_INFO_ID));
    }

    @Test
    @Order(4)
    void create_InvalidExtension_ThrowsException() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "files",
                "malicious.exe",
                "application/octet-stream",
                "dangerous payload".getBytes()
        );

        assertThrows(BadRequestException.class, () -> documentService.create(List.of(invalidFile), OBJECT_INFO_ID));
    }

    // ===================== GET LIST / SEARCH =====================

    @Test
    @Order(5)
    void getCDRContents_DefaultPagination() {
        List<Document> list = documentService.getDoc("", 0, 10, "creationTime", "desc", OBJECT_INFO_ID);
        logger.info("Documents count: {}", list.size());
        assertNotNull(list);
    }

    @Test
    @Order(6)
    void getCDRContents_WithKeyword() {
        List<Document> list = documentService.getDoc("lap_trinh", 0, 10, "creationTime", "desc", OBJECT_INFO_ID);
        logger.info("Documents by keyword: {}", list.size());
        assertNotNull(list);
    }

    @Test
    @Order(7)
    void getCDRContents_WithPagination() {
        List<Document> page1 = documentService.getDoc("", 0, 1, "creationTime", "desc", OBJECT_INFO_ID);
        List<Document> page2 = documentService.getDoc("", 1, 1, "creationTime", "desc", OBJECT_INFO_ID);
        assertNotNull(page1);
        assertNotNull(page2);
        logger.info("Page1 size: {}, Page2 size: {}", page1.size(), page2.size());
    }

    @Test
    @Order(8)
    void getDocCount_Success() {
        Integer count = documentService.getDocCount("", OBJECT_INFO_ID);
        logger.info("Total doc count for {}: {}", OBJECT_INFO_ID, count);
        assertNotNull(count);
        assertTrue(count >= 0);
    }

    // ===================== DELETE =====================

    @Test
    @Order(9)
    void delete_SoftDelete_Success() {
        if (createdDocumentId != null) {
            Map<String, String> response = documentService.delete(Map.of("id", createdDocumentId));
            assertNotNull(response);
            logger.info("Delete result: {}", response);

            // Verify that soft-deleted document is no longer active / found
            assertThrows(com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException.class,
                    () -> documentService.getById(createdDocumentId));
        }
    }

    // ===================== VALIDATION & EDGE CASES =====================

    @Test
    @Order(10)
    void create_SpecialCharactersInFileName_ThrowsException() {
        MockMultipartFile fileWithSpecialChar = new MockMultipartFile(
                "files",
                "bad#file@name!.pdf",
                "application/pdf",
                "test content".getBytes()
        );
        assertThrows(BadRequestException.class, () -> documentService.create(List.of(fileWithSpecialChar), OBJECT_INFO_ID));
    }

    @Test
    @Order(11)
    void create_FileNameTooLong_ThrowsException() {
        String veryLongName = "a".repeat(55) + ".pdf";
        MockMultipartFile fileTooLong = new MockMultipartFile(
                "files",
                veryLongName,
                "application/pdf",
                "test content".getBytes()
        );
        assertThrows(BadRequestException.class, () -> documentService.create(List.of(fileTooLong), OBJECT_INFO_ID));
    }

    @Test
    @Order(12)
    void create_EmptyAppIdOrNull_ThrowsException() {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "valid_doc.pdf",
                "application/pdf",
                "content".getBytes()
        );
        assertThrows(BadRequestException.class, () -> documentService.create(List.of(file), null));
        assertThrows(BadRequestException.class, () -> documentService.create(List.of(file), "   "));
    }

    @Test
    @Order(13)
    void create_EmptyFileContent_ThrowsException() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "files",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );
        assertThrows(BadRequestException.class, () -> documentService.create(List.of(emptyFile), OBJECT_INFO_ID));
    }

    @Test
    @Order(14)
    void delete_InvalidMapOrMissing_ThrowsException() {
        assertThrows(BadRequestException.class, () -> documentService.delete(null));
        assertThrows(com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException.class, () -> documentService.delete(Map.of()));
        assertThrows(com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException.class, () -> documentService.delete(Map.of("id", "  ")));
    }

    @Test
    @Order(15)
    void deleteById_NotFound_ThrowsException() {
        assertThrows(com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException.class,
                () -> documentService.deleteById("non-existent-uuid"));
    }

    @Test
    @Order(16)
    void getById_NotFound_ThrowsException() {
        assertThrows(com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException.class,
                () -> documentService.getById("non-existent-uuid"));
    }

    @Test
    @Order(17)
    void download_NotFound_ThrowsException() {
        assertThrows(BadRequestException.class, () -> documentService.download(null));
        assertThrows(BadRequestException.class, () -> documentService.download("   "));
        assertThrows(com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException.class,
                () -> documentService.downloadById("non-existent-id"));
    }

    @Test
    @Order(18)
    void queryMethods_PagedAndSorted_Success() {
        assertNotNull(documentService.getByAppId(OBJECT_INFO_ID));
        assertNotNull(documentService.getByAppIdPaged(OBJECT_INFO_ID, 0, 5, "creationTime", "desc"));
        assertNotNull(documentService.getAllActive());
        assertNotNull(documentService.getAllPaged(0, 5, "creationTime", "desc"));
        assertNotNull(documentService.getAllSorted("creationTime", "desc"));
        assertNotNull(documentService.searchBySpecificationPaged("name:cdr", 0, 5, "creationTime", "desc"));
    }
}
