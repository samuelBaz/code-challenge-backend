package com.example.backend.database.models;


import com.example.backend.database.dtos.DtoBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.*;
import java.util.stream.Collectors;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("rawtypes")
public class ModelBase<D extends DtoBase> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false)
    private Date updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    public Long getIndexedId() {
        if (null == id) {
            return 0L;
        }
        return id;
    }

    protected void beforeConversion(D element, ModelMapper mapper) {
        // Do nothing
    }

    protected void afterConversion(D element, ModelMapper mapper) {
        // Do nothing
    }

    @SuppressWarnings("unchecked")
    public <E extends ModelBase> E toDomain(D element, ModelMapper mapper) {
        try {
            return convert(element, mapper);
        } catch (Exception ex) {
            return (E) this;
        }
    }

    @SuppressWarnings("unchecked")
    protected <E extends ModelBase> E convert(D element, ModelMapper mapper) {
        beforeConversion(element, mapper);
        mapper.map(element, this);
        afterConversion(element, mapper);
        return (E) this;
    }

    public <E extends ModelBase> List<E> toListDomain(Collection<D> elements, ModelMapper mapper) {
        if (elements == null || elements.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return convert(elements, mapper);
        } catch (Exception ex) {
            return Collections.emptyList();
        }

    }

    @SuppressWarnings("unchecked")
    protected <E extends ModelBase> List<E> convert(Collection<D> elements, ModelMapper mapper) {
        return (List<E>) elements.stream().map(element -> {
            try {
                return this.getClass().newInstance().toDomain(element, mapper);
            } catch (InstantiationException | IllegalAccessException e) {
                return new ModelBase<>();
            }
        }).sorted(Comparator.comparing(ModelBase::getIndexedId)).collect(Collectors.toList());
    }



    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new java.util.Date();
    }
}