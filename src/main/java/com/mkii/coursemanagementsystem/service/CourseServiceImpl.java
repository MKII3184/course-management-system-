package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.entity.Course;
import com.mkii.coursemanagementsystem.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dao.CourseDao;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseDao courseDao;

    public CourseServiceImpl(CourseRepository courseRepository, CourseDao courseDao) {
        this.courseRepository = courseRepository;
        this.courseDao = courseDao;
    }

    // Hàm phụ trợ convert Entity -> ResponseDTO
    private CourseResponseDTO mapToDTO(Course course) {
        return new CourseResponseDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getPrice()
        );
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(MessageConstants.COURSE_NOT_FOUND, id)));
        return mapToDTO(course);
    }

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {
        Course course = new Course();
        course.setTitle(requestDTO.getTitle());
        course.setDescription(requestDTO.getDescription());
        course.setDuration(requestDTO.getDuration());
        course.setPrice(requestDTO.getPrice());

        Course savedCourse = courseDao.create(course);
        return mapToDTO(savedCourse);
    }

    @Override
    public List<CourseResponseDTO> searchCourses(String keyword) {
        return courseRepository.searchByTitle(keyword);
    }

    @Override
    public List<CourseResponseDTO> filterByPrice(Double minPrice, Double maxPrice) {
        return courseRepository.findByPriceRange(minPrice, maxPrice);
    }
}