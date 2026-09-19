package com.mkii.coursemanagementsystem.hibernateDao;

import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.dao.IGenericDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HibernateGenericDao<Pk, Entity> extends SimpleJpaRepository<Entity, Pk> implements IGenericDao<Pk, Entity> {

    private static final Logger logger = LoggerFactory.getLogger(HibernateGenericDao.class);

    private Class<Entity> type;
    private EntityManager entityManager;

    public HibernateGenericDao(Class<Entity> type, EntityManager entityManager) {
        super(type, entityManager);
        this.type = type;
        this.entityManager = entityManager;
    }

    public Class<Entity> getType() {
        return type;
    }

    public void setType(Class<Entity> type) {
        this.type = type;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Entity create(Entity anEntity) {
        if (anEntity == null) {
            logger.warn(MessageConstants.ENTITY_CANNOT_BE_NULL);
            throw new IllegalArgumentException(String.format(MessageConstants.CANNOT_CREATE_NULL_ENTITY, getType().getName()));
        }
        return save(anEntity);
    }

    @Override
    @Transactional
    public Entity update(Entity anEntity) {
        if (anEntity == null) {
            logger.warn(MessageConstants.ENTITY_CANNOT_BE_NULL);
            throw new IllegalArgumentException(String.format(MessageConstants.CANNOT_UPDATE_NULL_ENTITY, getType().getName()));
        }
        return saveAndFlush(anEntity);
    }

    @Override
    @Transactional
    public Entity saveOrUpdate(Entity anEntity) {
        if (anEntity == null) {
            logger.warn(MessageConstants.ENTITY_CANNOT_BE_NULL);
            throw new IllegalArgumentException(String.format(MessageConstants.CANNOT_SAVE_OR_UPDATE_NULL_ENTITY, getType().getName()));
        }
        return save(anEntity);
    }

    @Override
    @Transactional
    public void delete(Entity anEntity) {
        if (anEntity == null) {
            logger.warn(MessageConstants.ENTITY_CANNOT_BE_NULL);
            throw new IllegalArgumentException(String.format(MessageConstants.CANNOT_DELETE_NULL_ENTITY, getType().getName()));
        }
        super.delete(anEntity);
    }

    @Override
    @Transactional
    public void deleteById(Pk id) {
        if (id == null) {
            logger.warn(MessageConstants.ENTITY_CANNOT_BE_NULL);
            throw new IllegalArgumentException(String.format(MessageConstants.CANNOT_DELETE_NULL_ID, getType().getName()));
        }
        super.deleteById(id);
    }

    @Override
    public Entity get(Pk id) {
        if (id == null) {
            return null;
        }
        return super.findById(id).orElse(null);
    }

    @Override
    public Optional<Entity> findById(Pk id) {
        if (id == null) {
            return Optional.empty();
        }
        return super.findById(id);
    }

    @Override
    public List<Entity> getAll() {
        return findAll();
    }

    @Override
    public Page<Entity> getAll(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    public List<Entity> getAll(Sort sort) {
        return findAll(sort);
    }

    @Override
    @Transactional
    public List<Entity> createAll(List<Entity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteAll(List<Entity> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        super.deleteAll(entities);
    }

    @Override
    public boolean exists(Pk id) {
        if (id == null) {
            return false;
        }
        return existsById(id);
    }

    @Override
    public long count() {
        return super.count();
    }

    @Override
    public void flush() {
        super.flush();
    }

    @Override
    public void clear() {
        if (entityManager != null) {
            entityManager.clear();
        }
    }

    // JPQL / HQL Helper Methods
    public List<Entity> findByHql(String hql, Map<String, Object> params) {
        TypedQuery<Entity> query = entityManager.createQuery(hql, getType());
        if (params != null) {
            params.forEach(query::setParameter);
        }
        return query.getResultList();
    }

    @Transactional
    public int executeHql(String hql, Map<String, Object> params) {
        Query query = entityManager.createQuery(hql);
        if (params != null) {
            params.forEach(query::setParameter);
        }
        return query.executeUpdate();
    }
}
