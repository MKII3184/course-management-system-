package com.mkii.coursemanagementsystem.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IGenericDao<Pk, Entity> {

    @Transactional
    Entity create(Entity anEntity);

    @Transactional
    Entity update(Entity anEntity);

    @Transactional
    Entity saveOrUpdate(Entity anEntity);

    @Transactional
    void delete(Entity anEntity);

    @Transactional
    void deleteById(Pk id);

    Entity get(Pk id);

    Optional<Entity> findById(Pk id);

    List<Entity> getAll();

    Page<Entity> getAll(Pageable pageable);

    List<Entity> getAll(Sort sort);

    @Transactional
    List<Entity> createAll(List<Entity> entities);

    @Transactional
    void deleteAll(List<Entity> entities);

    boolean exists(Pk id);

    long count();

    void flush();

    void clear();
}
