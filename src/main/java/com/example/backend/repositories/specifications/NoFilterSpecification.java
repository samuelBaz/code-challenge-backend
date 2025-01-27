package com.example.backend.repositories.specifications;

import com.example.backend.database.models.ModelBase;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

@SuppressWarnings("rawtypes")
public class NoFilterSpecification<E extends ModelBase> implements Specification<E> {

    private static final long serialVersionUID = 1L;

    private static final String ID = "id";

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return builder.equal(root.get(ID), root.get(ID));
    }
}
