package com.example.backend.controllers;

import com.example.backend.database.dtos.DtoBase;
import com.example.backend.database.dtos.response.ResponseBase;
import com.example.backend.database.dtos.response.ResponseBaseEntity;
import com.example.backend.database.dtos.response.ResponseEntitiesPage;
import com.example.backend.database.models.ModelBase;
import com.example.backend.exceptions.DontDeleteException;
import com.example.backend.exceptions.InternalErrorException;
import com.example.backend.repositories.specifications.GenericSpecificationsBuilder;
import com.example.backend.service.IGenericService;
import com.example.backend.util.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings({"rawtypes", "unchecked"})
@CrossOrigin
public abstract class GenericController<E extends ModelBase, D extends DtoBase<E>> {

    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected ObjectMapper objectMapper;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @DeleteMapping(value = "/{id}")
    protected ResponseBaseEntity deleteElement(@PathVariable("id") @NotNull Long id) {
        try {
            ResponseBaseEntity responseBaseEntity = getService().deleteById(id);
            responseBaseEntity.setMessage(getTypeName() + " was successfully deleted.");
            return responseBaseEntity;
        } catch (DontDeleteException e) {
            logger.error(e.getMessage());
            ResponseBaseEntity responseBaseEntity = new ResponseBaseEntity(Status.DONT_DELETE);
            responseBaseEntity.setMessage(e.getMessage());
            return responseBaseEntity;
        } catch (Exception e) {
            String messageError = "An error occurred while delete " + getTypeName() + " with id: " + id;
            logger.error(messageError);
            throw new InternalErrorException(messageError, e);
        }
    }

    @GetMapping("/{id}")
    protected ResponseBaseEntity getById(@PathVariable("id") @NotNull Long id) {
        try {
            return getService().findById(id);
        } catch (NoResultException e) {
            logger.error(e.getMessage());
            throw new InternalErrorException(e.getMessage(), e);
        } catch (Exception e) {
            String messageError = "An error occurred while found entity with id " + getTypeName();
            logger.error(messageError);
            throw new InternalErrorException(messageError, e);
        }

    }

    @GetMapping
    protected ResponseEntitiesPage getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String filter
    ) {
        try {
            Sort sort = getSort(order);
            Specification<E> spec = new GenericSpecificationsBuilder<E>().build(filter);
            Pageable pageable = PageRequest.of(page, size, sort);
            ResponseEntitiesPage responseEntitiesPage = getService().findAll(pageable, spec);
            responseEntitiesPage.setMessage(getTypeName() + "s were successfully listed.");
            return responseEntitiesPage;
        } catch (Exception e) {
            String messageError = "An error occurred while found entities of " + getService().getClass().getTypeName();
            logger.error(messageError);
            throw new InternalErrorException(messageError, e);
        }
    }

    @PostMapping
    protected ResponseBaseEntity save(@RequestBody D element) {
        try {
            ResponseBaseEntity responseBaseEntity = getService().save(toModel(element));
            responseBaseEntity.setMessage(getTypeName() + " was successfully created.");
            return responseBaseEntity;
        } catch (Exception e) {
            logger.error("An error occurred while created " + getService().getClass().getTypeName());
            throw new InternalErrorException("An error occurred while created/updated.", e);
        }
    }

    @PostMapping("all")
    protected ResponseBase saveAll(@RequestBody Iterable<D> elements) {
        try {
            List<E> entities = StreamSupport.stream(elements.spliterator(), false).map((model) -> toModel(model)).collect(Collectors.toList());
            return getService().saveAll(entities);
        } catch (Exception e) {
            String messageError = "An error occurred while saved multiples entities of " + getService().getClass().getTypeName();
            logger.error(messageError);
            throw new InternalErrorException(messageError, e);
        }
    }

    @PutMapping
    protected ResponseBaseEntity update(@RequestBody D element) {
        try {
            ResponseBaseEntity responseBaseEntity = getService().save(toModel(element));
            responseBaseEntity.setMessage(getTypeName() + " was successfully updated.");
            return responseBaseEntity;
        } catch (Exception e) {
            logger.error("An error occurred while updated " + getService().getClass().getTypeName());
            throw new InternalErrorException("An error occurred while created/updated.", e);
        }
    }

    protected abstract IGenericService getService();

    private D getInstanceOfD() {
        Class<D> type = getDtoClass();
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new InternalErrorException("No default constructor.", e);
        }
    }

    private E getInstanceOfE() {
        Class<E> type = getDomainClass();
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

    private Class<E> getDomainClass() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<E>) superClass.getActualTypeArguments()[0];
    }

    protected D toDto(E entity) {
        return (D) getInstanceOfD().toDto(entity, modelMapper);
    }

    protected E toModel(D dto) {
        return (E) getInstanceOfE().toDomain(dto, modelMapper);
    }

    protected List<D> toDto(Collection<E> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    protected String getTypeName() {
        String typeName = (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0])
                .getTypeName();
        typeName = typeName.substring(typeName.lastIndexOf('.') + 1);
        return typeName;
    }


    protected Sort getSort(String order) {
        Sort sort;

        if (order == null || order.isEmpty()) {
            sort = Sort.by("id").descending();
        } else if (order.startsWith("-")) {
            String ordenField = order.substring(1);
            sort = Sort.by(ordenField).descending();
        } else {
            sort = Sort.by(order).ascending();
        }
        return sort;
    }
}
