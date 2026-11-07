package com.mkii.coursemanagementsystem.service;

import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MinioStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioStorageService minioStorageService;

    private static final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(minioStorageService, "bucketName", BUCKET_NAME);
    }

    // ==========================================
    // 1. ENSURE BUCKET EXISTS
    // ==========================================

    @Test
    @DisplayName("ensureBucketExists: Bucket already exists -> do not make bucket")
    void ensureBucketExists_AlreadyExists() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        minioStorageService.ensureBucketExists();

        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    @DisplayName("ensureBucketExists: Bucket does not exist -> make bucket")
    void ensureBucketExists_BucketDoesNotExist_CreatesBucket() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        minioStorageService.ensureBucketExists();

        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    @DisplayName("ensureBucketExists: Exception occurs -> throws RuntimeException")
    void ensureBucketExists_Exception_ThrowsRuntimeException() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> minioStorageService.ensureBucketExists());
        assertTrue(ex.getMessage().contains("Could not connect to MinIO storage"));
    }

    // ==========================================
    // 2. UPLOAD
    // ==========================================

    @Test
    @DisplayName("upload: Success with valid input")
    void upload_Success() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        byte[] content = "Hello MinIO".getBytes();
        InputStream is = new ByteArrayInputStream(content);

        assertDoesNotThrow(() -> minioStorageService.upload("documents/test.pdf", is, "application/pdf", content.length));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("upload: Success with null contentType defaults to application/octet-stream")
    void upload_NullContentType_Success() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        byte[] content = "Raw data".getBytes();
        InputStream is = new ByteArrayInputStream(content);

        assertDoesNotThrow(() -> minioStorageService.upload("documents/raw.bin", is, null, content.length));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("upload: Exception during putObject -> throws RuntimeException")
    void upload_Exception_ThrowsRuntimeException() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenThrow(new RuntimeException("Upload failed"));

        byte[] content = "Test".getBytes();
        InputStream is = new ByteArrayInputStream(content);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> minioStorageService.upload("test.pdf", is, "application/pdf", content.length));
        assertTrue(ex.getMessage().contains("Failed to upload file to MinIO"));
    }

    // ==========================================
    // 3. DOWNLOAD
    // ==========================================

    @Test
    @DisplayName("download: Success returning byte array")
    void download_Success() throws Exception {
        byte[] expected = "File content inside MinIO".getBytes();
        GetObjectResponse response = new GetObjectResponse(null, BUCKET_NAME, "region", "path.pdf", new ByteArrayInputStream(expected));

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        byte[] actual = minioStorageService.download("path.pdf");
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("download: Successfully decrypts AES encrypted object")
    void download_EncryptedSuccess() throws Exception {
        byte[] original = "Confidential Student Exam Paper".getBytes();
        javax.crypto.SecretKey key = com.mkii.coursemanagementsystem.util.AESUtil.getKeyFromPassword(
                "CourseManagementSystemSecretKey2026", "cms-salt-2026-v1");
        byte[] encrypted = com.mkii.coursemanagementsystem.util.AESUtil.encryptBytesWithIv(original, key);

        GetObjectResponse response = new GetObjectResponse(null, BUCKET_NAME, "region", "exam.pdf", new ByteArrayInputStream(encrypted));
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        byte[] actual = minioStorageService.download("exam.pdf");
        assertArrayEquals(original, actual);
    }

    @Test
    @DisplayName("download: Exception during getObject -> throws RuntimeException")
    void download_Exception_ThrowsRuntimeException() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("Object not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> minioStorageService.download("missing.pdf"));
        assertTrue(ex.getMessage().contains("Failed to download file from MinIO"));
    }

    // ==========================================
    // 4. DELETE
    // ==========================================

    @Test
    @DisplayName("delete: Success removing object")
    void delete_Success() throws Exception {
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertDoesNotThrow(() -> minioStorageService.delete("path.pdf"));
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("delete: Exception during removeObject -> throws RuntimeException")
    void delete_Exception_ThrowsRuntimeException() throws Exception {
        doThrow(new RuntimeException("Permission denied"))
                .when(minioClient).removeObject(any(RemoveObjectArgs.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> minioStorageService.delete("path.pdf"));
        assertTrue(ex.getMessage().contains("Failed to delete file from MinIO"));
    }
}
