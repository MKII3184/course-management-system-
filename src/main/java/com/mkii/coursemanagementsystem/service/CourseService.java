package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;

import java.util.List;

public interface CourseService {
    List<CourseResponseDTO> getAllCourses();
    CourseResponseDTO getCourseById(Long id);
    CourseResponseDTO createCourse(CourseRequestDTO requestDTO);
    List<CourseResponseDTO> searchCourses(String keyword);
    List<CourseResponseDTO> filterByPrice(Double minPrice, Double maxPrice);
}