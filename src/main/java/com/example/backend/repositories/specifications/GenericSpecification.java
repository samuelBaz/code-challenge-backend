package com.example.backend.repositories.specifications;

import com.example.backend.database.models.ModelBase;
import com.example.backend.exceptions.RepositoryException;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

@SuppressWarnings("rawtypes")
public class GenericSpecification<E extends ModelBase> implements Specification<E> {

    private static final long serialVersionUID = 1L;

    protected SearchCriteria criteria;

    public GenericSpecification(SearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria == null) {
            throw new RepositoryException("Specification criteria must not be null!");
        }
        query.distinct(true);
        switch (criteria.getOperation().toLowerCase()) {
            case "gt":
                return builder.greaterThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case "gte":
                return builder.greaterThanOrEqualTo(root.<String>get(criteria.getKey()),
                        criteria.getValue().toString());
            case "lt":
                return builder.lessThan(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case "lte":
                return builder.lessThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue().toString());
            case "eq":
                return getEqualPredicate(root, builder);
            default:
                throw new UnsupportedOperationException(
                        String.format("Could not recognize operation %s", criteria.getOperation().toLowerCase()));
        }
    }

    protected Predicate getEqualPredicate(Root<E> root, CriteriaBuilder builder) {
        if (root.get(criteria.getKey()).getJavaType() == String.class) {
            return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
        } else {
            return builder.equal(root.get(criteria.getKey()), criteria.getValue());
        }
    }

}
