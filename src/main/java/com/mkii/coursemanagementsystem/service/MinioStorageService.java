package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.util.AESUtil;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioStorageService {

    private static final Logger logger = LoggerFactory.getLogger(MinioStorageService.class);
    private static final SecretKey SECRET_KEY;

    static {
        try {
            SECRET_KEY = AESUtil.getKeyFromPassword("CourseManagementSystemSecretKey2026", "cms-salt-2026-v1");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Could not initialize AES SecretKey for MinIO: " + e.getMessage());
        }
    }

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name:documents}")
    private String bucketName;

    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                logger.info("MinIO bucket '{}' does not exist. Creating bucket...", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            logger.error("Error verifying/creating MinIO bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException(String.format(MessageConstants.MINIO_CONNECT_FAILED, e.getMessage()), e);
        }
    }

    public void upload(String objectPath, InputStream inputStream, String contentType, long size) {
        ensureBucketExists();
        try {
            byte[] rawBytes = IOUtils.toByteArray(inputStream);
            byte[] encryptedBytes = AESUtil.encryptBytesWithIv(rawBytes, SECRET_KEY);
            logger.info("Uploading AES-encrypted object '{}' to MinIO bucket '{}' (raw: {} bytes, encrypted: {} bytes)",
                    objectPath, bucketName, rawBytes.length, encryptedBytes.length);
            try (ByteArrayInputStream encryptedStream = new ByteArrayInputStream(encryptedBytes)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectPath)
                                .stream(encryptedStream, encryptedBytes.length, -1)
                                .contentType(contentType != null ? contentType : "application/octet-stream")
                                .build()
                );
            }
        } catch (Exception e) {
            logger.error("Error uploading object '{}' to MinIO: {}", objectPath, e.getMessage());
            throw new RuntimeException(String.format(MessageConstants.MINIO_UPLOAD_FAILED, e.getMessage()), e);
        }
    }

    public byte[] download(String objectPath) {
        try {
            logger.info("Downloading object '{}' from MinIO bucket '{}'", objectPath, bucketName);
            byte[] rawBytes;
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build())) {
                rawBytes = IOUtils.toByteArray(stream);
            }
            try {
                return AESUtil.decryptBytesWithIv(rawBytes, SECRET_KEY);
            } catch (Exception ex) {
                logger.warn("Could not decrypt object '{}', returning raw bytes: {}", objectPath, ex.getMessage());
                return rawBytes;
            }
        } catch (Exception e) {
            logger.error("Error downloading object '{}' from MinIO: {}", objectPath, e.getMessage());
            throw new RuntimeException(String.format(MessageConstants.MINIO_DOWNLOAD_FAILED, e.getMessage()), e);
        }
    }

    public void delete(String objectPath) {
        try {
            logger.info("Removing object '{}' from MinIO bucket '{}'", objectPath, bucketName);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Error deleting object '{}' from MinIO: {}", objectPath, e.getMessage());
            throw new RuntimeException(String.format(MessageConstants.MINIO_DELETE_FAILED, e.getMessage()), e);
        }
    }
}
