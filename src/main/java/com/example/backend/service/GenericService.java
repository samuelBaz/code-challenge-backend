package com.example.backend.service;

import com.example.backend.database.dtos.DtoBase;
import com.example.backend.database.dtos.response.ResponseBase;
import com.example.backend.database.dtos.response.ResponseBaseEntity;
import com.example.backend.database.dtos.response.ResponseEntitiesPage;
import com.example.backend.database.models.ModelBase;
import com.example.backend.exceptions.DontDeleteException;
import com.example.backend.exceptions.InternalErrorException;
import com.example.backend.repositories.IGenericRepository;
import com.example.backend.repositories.specifications.GenericSpecificationsBuilder;
import com.example.backend.util.Status;
import com.example.backend.util.StringUtility;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.NoResultException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@SuppressWarnings("rawtypes")
public abstract class GenericService<T extends ModelBase, D extends DtoBase<T>> implements IGenericService<T> {

    @Autowired
    protected ModelMapper modelMapper;

    protected Logger logger = LoggerFactory.getLogger(getInstanceOfE().getClass());

    @Override
    public ResponseEntitiesPage findAll(Pageable pageable, Specification specification) {
        ResponseEntitiesPage responseEntitiesPage = new ResponseEntitiesPage(Status.SUCCESSFUL);
        Page<T> page = getRepository().findAll(specification, pageable);
        responseEntitiesPage.setData(toDto(page));
        return responseEntitiesPage;
    }

    @Override
    @Transactional
    public ResponseBaseEntity findById(Long id) {
        ResponseBaseEntity responseBaseEntity;
        final Optional<T> optional = getRepository().findById(id);
        if (!optional.isPresent()) {
            String typeName = getTypeName();
            throw new NoResultException(String.format("%s Not found with id %s", typeName, id));
        } else {
            T dto = optional.get();
            responseBaseEntity = new ResponseBaseEntity(Status.SUCCESSFUL);
            responseBaseEntity.setData(toDto(dto));
            return responseBaseEntity;
        }
    }

    @Override
    @Transactional
    public ResponseBaseEntity save(T model) {
        logger.info(getTypeName() + ": " + toDto(model).toString() + " is trying update/save.");
        T t = getRepository().save(model);
        logger.info(getTypeName() + " with id " + t.getId() + " was successfully created/updated.");
        return findById(t.getId());
    }

    @Override
    public ResponseBaseEntity saveAndFlush(T model) {
        try {
            T t = getRepository().saveAndFlush(model);
            logger.info(getTypeName() + "with id " + t.getId() + "was successfully created.");
            return findById(t.getId());
        } catch (Exception e) {
            logger.error("An error occurred while created/updated " + getTypeName());
            throw new InternalErrorException("An error occurred while created/updated.", e);
        }
    }

    @Override
    public ResponseBase saveAll(Iterable<T> models) {
        StreamSupport.stream(models.spliterator(), false).map((model) -> getRepository().save(model)).collect(Collectors.toList());
        return new ResponseBase(Status.SUCCESSFUL);
    }

    @Override
    public ResponseBaseEntity deleteById(Long id) throws DontDeleteException {
        logger.info(getTypeName() + " with id: " + id + " is trying delete.");
        getRepository().deleteById(id);
        logger.info(getTypeName() + " with id: " + id + " was successfully deleted.");
        return new ResponseBaseEntity(Status.SUCCESSFUL);
    }

    protected Specification<T> getQueryFrom(String filter) {
        return new GenericSpecificationsBuilder<T>().build(filter);
    }

    protected String createFilter(Map<String, String> mappedValues) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : mappedValues.entrySet()) {
            if (!StringUtils.isEmpty(entry.getValue())) {
                builder.append(String.format("%s:eq:%s,", entry.getKey(), entry.getValue()));
            }
        }
        return StringUtility.removeTrailingComma(builder.toString());
    }

    protected abstract IGenericRepository<T> getRepository();

    private D getInstanceOfD() {
        Class<D> type = getDtoClass();
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new InternalErrorException("No default constructor.", e);
        }
    }

    private T getInstanceOfE() {
        Class<T> type = getDomainClass();
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new InternalErrorException("No default constructor.", e);
        }
    }

    private Class<D> getDtoClass() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<D>) superClass.getActualTypeArguments()[1];
    }

    private Class<T> getDomainClass() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) superClass.getActualTypeArguments()[0];
    }

    protected D toDto(T entity) {
        return (D) getInstanceOfD().toDto(entity, modelMapper);
    }

    protected T toModel(D dto) {
        return (T) getInstanceOfE().toDomain(dto, modelMapper);
    }

    protected List<D> toDto(Collection<T> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    protected Page<D> toDto(Page<T> entities) {
        return entities.map(this::toDto);
    }

    protected String getTypeName() {
        String typeName = (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0])
                .getTypeName();
        typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
        return typeName;
    }
}

