package com.mkii.coursemanagementsystem.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

    @Test
    @DisplayName("handleResourceNotFoundException returns 404")
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Entity not found: 100");
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Entity not found: 100", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    @DisplayName("handleBadRequestException returns 400")
    void testHandleBadRequestException() {
        BadRequestException ex = new BadRequestException("Invalid parameter provided");
        ResponseEntity<ErrorResponse> response = handler.handleBadRequestException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid parameter provided", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleValidationException returns 400 with formatted field errors")
    void testHandleValidationException() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "title", "Tiêu đề không được để trống"));
        bindingResult.addError(new FieldError("target", "price", "Giá phải lớn hơn 0"));

        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("sampleMethod", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Tiêu đề không được để trống"));
        assertTrue(response.getBody().getMessage().contains("Giá phải lớn hơn 0"));
    }

    @Test
    @DisplayName("handleTypeMismatchException returns 400 with parameter info")
    void testHandleTypeMismatchException() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Long.class, "id", null, null);

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatchException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("id"));
        assertTrue(response.getBody().getMessage().contains("abc"));
    }

    @Test
    @DisplayName("handleDataIntegrityViolationException returns 409 Conflict")
    void testHandleDataIntegrityViolationException() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate key entry 'test@gmail.com'");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolationException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Dữ liệu vi phạm ràng buộc"));
    }

    @Test
    @DisplayName("handleHttpMessageNotReadableException returns 400")
    void testHandleNotReadableException() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "Malformed JSON", new MockHttpInputMessage("{}".getBytes()));

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Dữ liệu gửi lên không đúng định dạng JSON"));
    }

    @Test
    @DisplayName("handleAccessDeniedException returns 403")
    void testHandleAccessDeniedException() {
        org.springframework.security.access.AccessDeniedException ex =
                new org.springframework.security.access.AccessDeniedException("Forbidden");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDeniedException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    @DisplayName("handleAuthenticationException returns 401")
    void testHandleAuthenticationException() {
        org.springframework.security.authentication.BadCredentialsException ex =
                new org.springframework.security.authentication.BadCredentialsException("Invalid password");

        ResponseEntity<ErrorResponse> response = handler.handleAuthenticationException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    @DisplayName("handleGenericException returns 500")
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected system crash");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Unexpected system crash", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test constructors for custom exceptions")
    void testExceptionConstructors() {
        ResourceNotFoundException rnf = new ResourceNotFoundException("Not found", new RuntimeException("root cause"));
        assertEquals("Not found", rnf.getMessage());
        assertNotNull(rnf.getCause());

        BadRequestException bre = new BadRequestException("Bad request", new RuntimeException("root cause"));
        assertEquals("Bad request", bre.getMessage());
        assertNotNull(bre.getCause());
    }

    @SuppressWarnings("unused")
    private void sampleMethod(String param) {}
}
