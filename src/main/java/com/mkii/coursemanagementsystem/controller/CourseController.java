package com.mkii.coursemanagementsystem.controller;

import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponseDTO> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public CourseResponseDTO getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    public CourseResponseDTO createCourse(@RequestBody CourseRequestDTO requestDTO) {
        return courseService.createCourse(requestDTO);
    }

    // API Query 1: Tìm kiếm theo từ khóa tên
    // Ví dụ URL: http://localhost:8080/api/courses/search?keyword=Spring
    @GetMapping("/search")
    public List<CourseResponseDTO> searchCourses(@RequestParam String keyword) {
        return courseService.searchCourses(keyword);
    }

    // API Query 2: Lọc theo khoảng giá
    // Ví dụ URL: http://localhost:8080/api/courses/filter?minPrice=500000&maxPrice=1500000
    @GetMapping("/filter")
    public List<CourseResponseDTO> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return courseService.filterByPrice(minPrice, maxPrice);
    }
}