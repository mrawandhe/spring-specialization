package com.example.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.dto.SearchFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class GenericSpecification<T> implements Specification<T> {

    private final List<SearchFilter> filters;

    public GenericSpecification(List<SearchFilter> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        for (SearchFilter filter : filters) {
            Path<?> path = root.get(filter.columnName());
            Object value = filter.value();

            switch (filter.operation()) {
                case "equal" -> predicates.add(cb.equal(path, value));
                case "notEqual" -> predicates.add(cb.notEqual(path, value));
                case "like" -> predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                case "greaterThan" -> predicates.add(cb.greaterThan(path.as(Comparable.class), (Comparable) value));
                case "lessThan" -> predicates.add(cb.lessThan(path.as(Comparable.class), (Comparable) value));
                case "greaterThanEqual" -> predicates.add(cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) value));
                case "lessThanEqual" -> predicates.add(cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) value));
                case "in" -> predicates.add(path.in((Collection<?>) value));
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}

