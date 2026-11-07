package com.mkii.coursemanagementsystem.common;

/**
 * Standard enterprise constants for Roles, Authorities, and Spring Security PreAuthorize expressions.
 * Used across Controllers, Services, and Security Configurations to eliminate magic strings.
 */
public final class RoleConstants {

    private RoleConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // --- Role Names (Without ROLE_ prefix) ---
    public static final String ADMIN = "ADMIN";
    public static final String LECTURER = "LECTURER";
    public static final String STUDENT = "STUDENT";
    public static final String ANONYMOUS = "ANONYMOUS";

    // --- Spring Security Role Prefixes (Default) ---
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_LECTURER = "ROLE_LECTURER";
    public static final String ROLE_STUDENT = "ROLE_STUDENT";

    // --- PreAuthorize SpEL Expressions ---
    public static final String HAS_ROLE_ADMIN = "hasRole('" + ADMIN + "')";
    public static final String HAS_ROLE_LECTURER = "hasRole('" + LECTURER + "')";
    public static final String HAS_ROLE_STUDENT = "hasRole('" + STUDENT + "')";

    public static final String HAS_ANY_ADMIN_LECTURER = "hasRole('" + ADMIN + "') or hasRole('" + LECTURER + "')";
    public static final String HAS_ANY_AUTHENTICATED_ROLE = "hasRole('" + ADMIN + "') or hasRole('" + LECTURER + "') or hasRole('" + STUDENT + "')";

    // --- Granular Permissions (Authorities) ---
    public static final String PERM_READ = "READ";
    public static final String PERM_WRITE = "WRITE";
    public static final String PERM_UPDATE = "UPDATE";
    public static final String PERM_DELETE = "DELETE";
}
