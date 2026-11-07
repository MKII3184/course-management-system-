package com.mkii.coursemanagementsystem.service.impl;

import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.common.exception.ValidationException;
import com.mkii.coursemanagementsystem.dao.IStudentDao;
import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Student;
import com.mkii.coursemanagementsystem.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    // Call ONLY IStudentDao (in dao package), never call Hibernate DAO directly
    @Autowired
    private IStudentDao studentDao;

    @Override
    public Student create(StudentRequestDTO requestDTO) {
        logger.info("Creating Student: {}", requestDTO.getFullName());
        if (requestDTO.getEmail() != null) {
            Student existingByEmail = studentDao.getByEmail(requestDTO.getEmail());
            if (existingByEmail != null) {
                throw new com.mkii.coursemanagementsystem.common.exception.BadRequestException(
                        String.format(MessageConstants.EMAIL_ALREADY_EXISTS, requestDTO.getEmail()));
            }
        }
        Student student = new Student();
        mapRequestToEntity(requestDTO, student);
        return studentDao.save(student);
    }

    @Override
    public Student update(StudentRequestDTO requestDTO, Long id) {
        logger.info("Updating Student: id={}, requestDTO={}", id, requestDTO);
        if (id == null) {
            throw new ValidationException(MessageConstants.ID_FIELD_CANNOT_BE_EMPTY);
        }
        Student existing = studentDao.findByPk(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id));
        }
        if (requestDTO.getEmail() != null && !requestDTO.getEmail().equalsIgnoreCase(existing.getEmail())) {
            Student existingByEmail = studentDao.getByEmail(requestDTO.getEmail());
            if (existingByEmail != null && !existingByEmail.getId().equals(id)) {
                throw new com.mkii.coursemanagementsystem.common.exception.BadRequestException(
                        String.format(MessageConstants.EMAIL_ALREADY_EXISTS, requestDTO.getEmail()));
            }
        }
        mapRequestToEntity(requestDTO, existing);
        return studentDao.update(existing);
    }

    @Override
    public Map<String, String> update(Map<String, Object> requestMap) {
        logger.info("requestMap for Student update is: {}", requestMap);
        if (requestMap == null || !requestMap.containsKey("id") || requestMap.get("id") == null || requestMap.get("id").toString().isBlank()) {
            throw new ValidationException(MessageConstants.ID_FIELD_CANNOT_BE_EMPTY);
        }
        Long id;
        try {
            id = Long.valueOf(String.valueOf(requestMap.get("id")).trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format(MessageConstants.PARAM_TYPE_MISMATCH, "id", requestMap.get("id")));
        }

        Student student = studentDao.findByPk(id);
        if (student == null) {
            throw new ValidationException(String.format(MessageConstants.NO_RECORD_WITH_SUCH_ID, id));
        }

        if (requestMap.containsKey("fullName") && requestMap.get("fullName") != null) {
            student.setFullName(String.valueOf(requestMap.get("fullName")).trim());
        }
        if (requestMap.containsKey("email") && requestMap.get("email") != null) {
            String newEmail = String.valueOf(requestMap.get("email")).trim();
            if (!newEmail.equalsIgnoreCase(student.getEmail())) {
                Student existingByEmail = studentDao.getByEmail(newEmail);
                if (existingByEmail != null && !existingByEmail.getId().equals(id)) {
                    throw new com.mkii.coursemanagementsystem.common.exception.BadRequestException(
                            String.format(MessageConstants.EMAIL_ALREADY_EXISTS, newEmail));
                }
            }
            student.setEmail(newEmail);
        }
        if (requestMap.containsKey("phone") && requestMap.get("phone") != null) {
            student.setPhone(String.valueOf(requestMap.get("phone")).trim());
        }
        if (requestMap.containsKey("address")) {
            student.setAddress(requestMap.get("address") != null ? String.valueOf(requestMap.get("address")).trim() : null);
        }

        studentDao.update(student);
        return Map.of(MessageConstants.STATUS, MessageConstants.SUCCESS, MessageConstants.MESSAGE, MessageConstants.STUDENT_UPDATED_SUCCESS);
    }

    @Override
    public Map<String, String> delete(Long id) {
        logger.info("Deleting Student: {}", id);
        if (id == null) {
            throw new ValidationException(MessageConstants.ID_FIELD_CANNOT_BE_EMPTY);
        }
        Student existing = studentDao.findByPk(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id));
        }
        studentDao.delete(id);
        return Map.of(MessageConstants.STATUS, MessageConstants.SUCCESS, MessageConstants.MESSAGE, MessageConstants.STUDENT_DELETED_SUCCESS);
    }

    @Override
    public Map<String, String> delete(Map<String, String> requestMap) {
        logger.info("Deleting Student by Map: {}", requestMap);
        if (requestMap == null || !requestMap.containsKey("id") || requestMap.get("id") == null || requestMap.get("id").isBlank()) {
            throw new ValidationException(MessageConstants.ID_FIELD_CANNOT_BE_EMPTY);
        }
        try {
            Long id = Long.parseLong(requestMap.get("id").trim());
            return delete(id);
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format(MessageConstants.PARAM_TYPE_MISMATCH, "id", requestMap.get("id")));
        }
    }

    @Override
    public StudentResponseDTO getById(Long id) {
        logger.info("Getting Student by id: {}", id);
        Student student = studentDao.getById(id);
        if (student == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id));
        }
        return mapToDTO(student);
    }

    @Override
    public List<StudentResponseDTO> getAll() {
        logger.info("Getting all Students");
        return studentDao.getAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResponseDTO> searchByKeyword(String keyword) {
        logger.info("Searching Students by keyword: {}", keyword);
        return studentDao.searchByKeyword(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDTO getByEmail(String email) {
        logger.info("Getting Student by email: {}", email);
        Student student = studentDao.getByEmail(email);
        if (student == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND_EMAIL, email));
        }
        return mapToDTO(student);
    }

    @Override
    public StudentResponseDTO getByField(String field, Object value) {
        logger.info("Getting Student by field: {} = {}", field, value);
        Student student = studentDao.getByField(field, value);
        if (student == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND_FIELD, field, value));
        }
        return mapToDTO(student);
    }

    @Override
    public List<StudentResponseDTO> findByFieldList(String field, Object value) {
        logger.info("Finding Student list by field: {} = {}", field, value);
        return studentDao.findByField(field, value)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentResponseDTO> findByFieldPaged(String field, Object value, int page, int size, String sortBy, String direction) {
        logger.info("Finding Student page by field: {} = {}, page={}, size={}, sort={} {}", field, value, page, size, sortBy, direction);
        return studentDao.findByFieldPaged(field, value, page, size, sortBy, direction).map(this::mapToDTO);
    }

    @Override
    public List<StudentResponseDTO> createBatch(List<StudentRequestDTO> requestDTOs) {
        logger.info("Creating batch of {} Students", requestDTOs != null ? requestDTOs.size() : 0);
        if (requestDTOs == null || requestDTOs.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<Student> entities = requestDTOs.stream().map(dto -> {
            Student student = new Student();
            mapRequestToEntity(dto, student);
            return student;
        }).collect(Collectors.toList());
        return studentDao.createAll(entities)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ──────────── Search, Sorting & Pagination (Delegated to IStudentDao) ────────────

    @Override
    public List<StudentResponseDTO> searchBySpecification(String search) {
        logger.info("Searching Students by Specification query: {}", search);
        return studentDao.searchBySpecification(search)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResponseDTO> getAllSorted(String sortBy, String direction) {
        logger.info("Getting all Students sorted by {} {}", sortBy, direction);
        return studentDao.findAllSorted(sortBy, direction)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentResponseDTO> getAllPaged(int page, int size, String sortBy, String direction) {
        logger.info("Getting all Students paged: page={}, size={}, sort={} {}", page, size, sortBy, direction);
        return studentDao.findAllPaged(page, size, sortBy, direction).map(this::mapToDTO);
    }

    @Override
    public List<StudentResponseDTO> searchBySpecificationSorted(String search, String sortBy, String direction) {
        logger.info("Searching Students by spec sorted: search={}, sort={} {}", search, sortBy, direction);
        return studentDao.searchBySpecificationSorted(search, sortBy, direction)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentResponseDTO> searchBySpecificationPaged(String search, int page, int size, String sortBy, String direction) {
        logger.info("Searching Students by spec paged: search={}, page={}, size={}, sort={} {}", search, page, size, sortBy, direction);
        return studentDao.searchBySpecificationPaged(search, page, size, sortBy, direction).map(this::mapToDTO);
    }

    @Override
    public Integer getCount() {
        logger.info("Getting Student count");
        return studentDao.getCount();
    }

    @Override
    public Map<String, Object> getCountMap() {
        logger.info("Getting Student count map");
        Integer count = getCount();
        return Map.of(MessageConstants.STATUS, MessageConstants.SUCCESS, MessageConstants.COUNT, count);
    }

    // ──────────── Private helpers ────────────

    private void mapRequestToEntity(StudentRequestDTO requestDTO, Student student) {
        if (requestDTO.getFullName() != null) {
            student.setFullName(requestDTO.getFullName());
        }
        if (requestDTO.getEmail() != null) {
            student.setEmail(requestDTO.getEmail());
        }
        if (requestDTO.getPhone() != null) {
            student.setPhone(requestDTO.getPhone());
        }
        if (requestDTO.getAddress() != null) {
            student.setAddress(requestDTO.getAddress());
        }
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
}
