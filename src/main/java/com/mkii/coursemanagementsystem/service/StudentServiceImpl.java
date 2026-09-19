package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dao.StudentDao;
import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Student;
import com.mkii.coursemanagementsystem.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentDao studentDao;

    public StudentServiceImpl(StudentRepository studentRepository, StudentDao studentDao) {
        this.studentRepository = studentRepository;
        this.studentDao = studentDao;
    }

    private StudentResponseDTO mapToDTO(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getPhone(),
                student.getAddress()
        );
    }

    @Override
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id)));
        return mapToDTO(student);
    }

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        Student student = new Student();
        student.setFullName(requestDTO.getFullName());
        student.setEmail(requestDTO.getEmail());
        student.setPhone(requestDTO.getPhone());
        student.setAddress(requestDTO.getAddress());

        Student savedStudent = studentDao.create(student);
        return mapToDTO(savedStudent);
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id)));

        student.setFullName(requestDTO.getFullName());
        student.setEmail(requestDTO.getEmail());
        student.setPhone(requestDTO.getPhone());
        student.setAddress(requestDTO.getAddress());

        Student updatedStudent = studentRepository.save(student);
        return mapToDTO(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id));
        }
        studentRepository.deleteById(id);
    }

    @Override
    public List<StudentResponseDTO> searchStudents(String keyword) {
        return studentRepository.searchByKeyword(keyword);
    }
}
