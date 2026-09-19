package com.mkii.coursemanagementsystem.common;

public final class MessageConstants {

    private MessageConstants() {
        // Prevent instantiation
    }

    // Generic Entity Validation Messages
    public static final String ENTITY_CANNOT_BE_NULL = "Cannot process a null entity.";
    public static final String CANNOT_CREATE_NULL_ENTITY = "Cannot create null entity[%s]";
    public static final String CANNOT_UPDATE_NULL_ENTITY = "Cannot update null entity[%s]";
    public static final String CANNOT_DELETE_NULL_ENTITY = "Cannot delete null entity[%s]";
    public static final String CANNOT_SAVE_OR_UPDATE_NULL_ENTITY = "Cannot save or update null entity[%s]";
    public static final String CANNOT_DELETE_NULL_ID = "Cannot delete entity with null id[%s]";

    // Business Exception Messages
    public static final String COURSE_NOT_FOUND = "Không tìm thấy khóa học với ID: %d";
    public static final String STUDENT_NOT_FOUND = "Không tìm thấy sinh viên với ID: %d";
}
