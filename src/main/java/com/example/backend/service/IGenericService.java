package com.example.backend.service;

import com.example.backend.database.dtos.response.ResponseBase;
import com.example.backend.database.dtos.response.ResponseBaseEntity;
import com.example.backend.database.dtos.response.ResponseEntitiesPage;
import com.example.backend.database.models.ModelBase;
import com.example.backend.exceptions.DontDeleteException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings("rawtypes")
public interface IGenericService<T extends ModelBase> {

    ResponseEntitiesPage findAll(Pageable pageable, Specification specification);

    ResponseBaseEntity findById(Long id);

    ResponseBaseEntity save(T model);

    ResponseBaseEntity saveAndFlush(T model);

    ResponseBase saveAll(Iterable<T> models);

    ResponseBaseEntity deleteById(Long id) throws DontDeleteException;

}