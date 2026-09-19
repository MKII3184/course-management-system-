package com.mkii.coursemanagementsystem.dao.impl;

import com.mkii.coursemanagementsystem.dao.CourseDao;
import com.mkii.coursemanagementsystem.entity.Course;
import com.mkii.coursemanagementsystem.hibernateDao.HibernateGenericDao;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class CourseDaoImpl extends HibernateGenericDao<Long, Course> implements CourseDao {

    public CourseDaoImpl(EntityManager entityManager) {
        super(Course.class, entityManager);
    }
}
