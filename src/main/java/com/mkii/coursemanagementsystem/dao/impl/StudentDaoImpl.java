package com.mkii.coursemanagementsystem.dao.impl;

import com.mkii.coursemanagementsystem.dao.StudentDao;
import com.mkii.coursemanagementsystem.entity.Student;
import com.mkii.coursemanagementsystem.hibernateDao.HibernateGenericDao;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDaoImpl extends HibernateGenericDao<Long, Student> implements StudentDao {

    public StudentDaoImpl(EntityManager entityManager) {
        super(Student.class, entityManager);
    }
}
