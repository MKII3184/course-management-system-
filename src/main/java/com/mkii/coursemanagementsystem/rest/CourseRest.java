package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.BASE_COURSE_URL)
public class CourseRest {

    private final CourseService courseService;

    public CourseRest(CourseService courseService) {
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

    @GetMapping("/search")
    public List<CourseResponseDTO> searchCourses(@RequestParam String keyword) {
        return courseService.searchCourses(keyword);
    }

    @GetMapping("/filter")
    public List<CourseResponseDTO> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return courseService.filterByPrice(minPrice, maxPrice);
    }
}
