package com.mkii.coursemanagementsystem.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.dto.DecryptRequestDTO;
import com.mkii.coursemanagementsystem.dto.DecryptResponseDTO;
import com.mkii.coursemanagementsystem.dto.EncryptRequestDTO;
import com.mkii.coursemanagementsystem.dto.EncryptResponseDTO;
import com.mkii.coursemanagementsystem.service.MinioStorageService;
import com.mkii.coursemanagementsystem.service.SecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityRestImplTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private MinioStorageService minioStorageService;

    @Test
    @DisplayName("Admin can encrypt payload - 200 OK")
    void testEncrypt_Admin_Success() throws Exception {
        EncryptRequestDTO request = EncryptRequestDTO.builder().data("SecretMessage").build();
        EncryptResponseDTO response = EncryptResponseDTO.builder()
                .encryptedData("EncryptedBase64WithIv")
                .algorithm("AES/CBC/PKCS5Padding")
                .build();

        when(securityService.encrypt(any(EncryptRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(ApiConstants.BASE_SECURITY_URL + "/encrypt")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.encryptedData").value("EncryptedBase64WithIv"))
                .andExpect(jsonPath("$.algorithm").value("AES/CBC/PKCS5Padding"));
    }

    @Test
    @DisplayName("Lecturer can decrypt payload - 200 OK")
    void testDecrypt_Lecturer_Success() throws Exception {
        DecryptRequestDTO request = DecryptRequestDTO.builder().encryptedData("EncryptedBase64WithIv").build();
        DecryptResponseDTO response = DecryptResponseDTO.builder().decryptedData("SecretMessage").build();

        when(securityService.decrypt(any(DecryptRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(ApiConstants.BASE_SECURITY_URL + "/decrypt")
                        .with(httpBasic("lecturer", "lecturer123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decryptedData").value("SecretMessage"));
    }

    @Test
    @DisplayName("Student is forbidden from encrypt endpoint - 403 Forbidden")
    void testEncrypt_Student_Forbidden() throws Exception {
        EncryptRequestDTO request = EncryptRequestDTO.builder().data("SecretMessage").build();

        mockMvc.perform(post(ApiConstants.BASE_SECURITY_URL + "/encrypt")
                        .with(httpBasic("student", "student123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Unauthenticated request without required role is forbidden - 403 Forbidden")
    void testEncrypt_Unauthenticated_Forbidden() throws Exception {
        EncryptRequestDTO request = EncryptRequestDTO.builder().data("SecretMessage").build();

        mockMvc.perform(post(ApiConstants.BASE_SECURITY_URL + "/encrypt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
