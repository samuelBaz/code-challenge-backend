package com.example.backend.database.dtos;

import com.example.backend.database.models.ModelBase;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class DtoBase<E extends ModelBase> {
    @JsonIgnore
    protected Logger logger = LoggerFactory.getLogger(DtoBase.class);
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Date updatedAt;
    private long version;

    protected void beforeConversion(E element, ModelMapper mapper) {
        // Do nothing
    }

    protected void afterConversion(E element, ModelMapper mapper) {
        // Do nothing
    }

    @SuppressWarnings("unchecked")
    public <D extends DtoBase> D toDto(E element, ModelMapper mapper) {
        if (element == null) {
            return (D) this;
        }
        return convert(element, mapper);
    }

    @SuppressWarnings("unchecked")
    protected <D extends DtoBase> D convert(E element, ModelMapper mapper) {
        beforeConversion(element, mapper);
        try {
            mapper.map(element, this);
        } catch (Exception ex) {
            setId(element.getId());
            logger.error("Error mapping", ex);
            return (D) this;
        }
        afterConversion(element, mapper);
        return (D) this;
    }

    public <D extends DtoBase> List<D> toListDto(Collection<E> elements, ModelMapper mapper) {
        if (elements == null || elements.isEmpty()) {
            return Collections.emptyList();
        }
        return convert(elements, mapper);
    }

    public <D extends DtoBase> Set<D> toSetDto(Set<E> elements, ModelMapper mapper) {
        if (elements == null || elements.isEmpty()) {
            return Collections.emptySet();
        }
        return convertToSet(elements, mapper);
    }

    @SuppressWarnings("unchecked")
    protected <D extends DtoBase> Set<D> convertToSet(Collection<E> elements, ModelMapper mapper) {
        return (Set<D>) elements.stream().map(element -> {
            try {
                return this.getClass().newInstance().toDto(element, mapper);
            } catch (InstantiationException | IllegalAccessException e) {
                return new DtoBase<>();
            }
        }).sorted(Comparator.comparing(DtoBase::getId)).collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    protected <D extends DtoBase> List<D> convert(Collection<E> elements, ModelMapper mapper) {
        return (List<D>) elements.stream().map(element -> {
            try {
                return this.getClass().newInstance().toDto(element, mapper);
            } catch (InstantiationException | IllegalAccessException e) {
                return new DtoBase<>();
            }
        }).sorted(Comparator.comparing(DtoBase::getId)).collect(Collectors.toList());
    }

}
