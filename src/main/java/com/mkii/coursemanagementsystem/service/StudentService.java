package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;

import java.util.List;

public interface StudentService {
    List<StudentResponseDTO> getAllStudents();
    StudentResponseDTO getStudentById(Long id);
    StudentResponseDTO createStudent(StudentRequestDTO requestDTO);
    StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO);
    void deleteStudent(Long id);
    List<StudentResponseDTO> searchStudents(String keyword);
}
