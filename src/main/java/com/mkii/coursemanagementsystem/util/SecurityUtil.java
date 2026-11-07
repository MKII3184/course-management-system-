package com.mkii.coursemanagementsystem.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Technical helper utility providing static methods to access Spring Security Context,
 * authenticated user details, roles, and authorization checks.
 *
 * All methods are thread-safe, purely technical, and contain no business logic.
 */
public final class SecurityUtil {

    public static final String ANONYMOUS_USER = "anonymousUser";
    public static final String ROLE_PREFIX = "ROLE_";

    private SecurityUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Retrieves the current Authentication object from the SecurityContext.
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context != null ? context.getAuthentication() : null;
    }

    /**
     * Checks if the current request is authenticated.
     */
    public static boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated() && !ANONYMOUS_USER.equalsIgnoreCase(auth.getName());
    }

    /**
     * Retrieves the username of the currently authenticated user.
     * Returns Optional.empty() if not authenticated or anonymous.
     */
    public static Optional<String> getCurrentUsername() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.ofNullable(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            String username = (String) principal;
            return ANONYMOUS_USER.equalsIgnoreCase(username) ? Optional.empty() : Optional.of(username);
        }
        return Optional.ofNullable(auth.getName());
    }

    /**
     * Retrieves the list of role/authority names assigned to the current user.
     * Strips "ROLE_" prefix if present for clean matching.
     */
    public static List<String> getCurrentUserRoles() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Collections.emptyList();
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith(ROLE_PREFIX) ? role.substring(ROLE_PREFIX.length()) : role)
                .collect(Collectors.toList());
    }

    /**
     * Checks if the current authenticated user has the specified role.
     */
    public static boolean hasRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        String cleanRole = role.startsWith(ROLE_PREFIX) ? role.substring(ROLE_PREFIX.length()) : role;
        return getCurrentUserRoles().stream().anyMatch(r -> r.equalsIgnoreCase(cleanRole));
    }

    /**
     * Checks if the current authenticated user has any of the specified roles.
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        for (String r : roles) {
            if (hasRole(r)) {
                return true;
            }
        }
        return false;
    }
}
