package com.mkii.coursemanagementsystem.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();
    private final PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
    private final UserDetailsService userDetailsService = securityConfig.userDetailsService(passwordEncoder);

    @Test
    void testAdminUserCredentialsAndRoles() {
        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        assertNotNull(admin);
        assertTrue(passwordEncoder.matches("admin123", admin.getPassword()));
        assertTrue(admin.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(admin.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LECTURER")));
        assertTrue(admin.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    void testLecturerUserCredentialsAndRoles() {
        UserDetails lecturer = userDetailsService.loadUserByUsername("lecturer");
        assertNotNull(lecturer);
        assertTrue(passwordEncoder.matches("lecturer123", lecturer.getPassword()));
        assertFalse(lecturer.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(lecturer.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LECTURER")));
        assertTrue(lecturer.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    void testStudentUserCredentialsAndRoles() {
        UserDetails student = userDetailsService.loadUserByUsername("student");
        assertNotNull(student);
        assertTrue(passwordEncoder.matches("student123", student.getPassword()));
        assertFalse(student.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertFalse(student.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_LECTURER")));
        assertTrue(student.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
    }
}
