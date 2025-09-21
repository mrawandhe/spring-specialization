package com.example.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.dto.SearchFilter;
import com.example.entity.Employee;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class EmployeeSpecification {

	
    public static Specification<Employee> searchByText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.trim().isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + searchText.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("firstName")), likePattern),
                cb.like(cb.lower(root.get("lastName")), likePattern),
                cb.like(cb.lower(root.get("email")), likePattern),
                cb.like(cb.lower(root.join("department").get("departmentName")), likePattern)
            );
        };
    }
    
    
    /** The below specification generates below query.
		SELECT emp.id, emp.created_at, cb.id, cb.email, cb.password, cb.status, cb.username,
		 dept.id, dept.budget, dept.created_at, dept.created_by, dept.department_name,
		 dept.location, dept.manager, emp.email, emp.first_name, emp.hire_date, emp.last_name,
		 emp.salary 
		FROM employees emp 
		JOIN departments dept ON dept.id=emp.department_id 
		JOIN users cb ON cb.id=emp.created_by  
		WHERE lower(cb.username) LIKE replace(?, '\\', '\\\\');

		root.fetch() performs an eager fetch of the associated entity, instructing Hibernate to
		  retrieve it in the same query as the root entity—eliminating the need for a separate SQL query.
     */
    public static Specification<Employee> empByDeptCreatedBy(String userName) {
        return (root, query, cb) -> {
            if (userName == null || userName.trim().isEmpty()) {
                return cb.conjunction();
            }

            // Fetch and reuse
            Fetch<?, ?> deptFetch = root.fetch("department", JoinType.INNER);
            Join<?, ?> deptJoin = (Join<?, ?>) deptFetch;

            Fetch<?, ?> userFetch = deptJoin.fetch("createdBy", JoinType.INNER);
            Join<?, ?> userJoin = (Join<?, ?>) userFetch;

            return cb.like(cb.lower(userJoin.get("username")), userName.toLowerCase());
        };
    }
    
    
    /** The following specification configures lazy loading for the department and createdBy associations,
        causing Hibernate to issue separate queries to fetch their details only when they are explicitly accessed.
     */
    public static Specification<Employee> empByDeptCreatedByLazyMultipleQueries (String userName) {
        return (root, query, cb) -> {
            if (userName == null || userName.trim().isEmpty()) {
                return cb.conjunction();
            }
            Join<?,?> deptJoin = root.join("department", JoinType.INNER);
            var userJoin = deptJoin.join("createdBy", JoinType.INNER);
            return cb.or(
                cb.like(cb.lower(userJoin.get("username")), userName)
            );
        };
    }
    
    
    
    public static Specification<Employee> byFilters(List<SearchFilter> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            for (SearchFilter filter : filters) {
                String column = filter.columnName();
                String value = (String) filter.value();

                switch (filter.operation()) {
                    case "equal" -> predicates.add(cb.equal(root.get(column), value));
                    case "notEqual" -> predicates.add(cb.notEqual(root.get(column), value));
                    case "like" -> predicates.add(cb.like(cb.lower(root.get(column).as(String.class)),
                    					"%" + value.toString().toLowerCase() + "%"));
                    case "in" -> predicates.add(root.get(column).in(value.split(",")));
                    case "greaterThan" -> predicates.add(cb.greaterThan(root.get(column), value));
                    case "lessThan" -> predicates.add(cb. lessThanOrEqualTo(root.get(column), value));
                    case "greaterThanEqual" -> predicates.add(cb.greaterThanOrEqualTo(root.get(column), value));
                    case "lessThanEqual" -> predicates.add(cb.lessThan(root.get(column), value));
                    default -> throw new UnsupportedOperationException("Op not supported: " + filter.operation());
                }
            }
//            return cb.or(predicates.toArray(new Predicate[0])); // Use 'OR' as per use case 
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }    
    
    
    public static Specification<Employee> byFiltersWithJoin(List<SearchFilter> filters) {
        return (root, query, cb) -> {
            if (filters == null || filters.isEmpty()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            for (SearchFilter filter : filters) {
                String column = filter.columnName();
                Object value = filter.value();

                Path<?> path;

                // handle nested joins
                if (column.contains(".")) {
                    String[] parts = column.split("\\.");
                    From<?, ?> join = root;

                    for (int i = 0; i < parts.length - 1; i++) {
                        join = join.join(parts[i], JoinType.INNER);
                    }
                    path = join.get(parts[parts.length - 1]);
                } else {
                    path = root.get(column);
                }

                switch (filter.operation()) {
                    case "equal" -> predicates.add(cb.equal(path, value));
                    case "notEqual" -> predicates.add(cb.notEqual(path, value));
                    case "like" -> predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                    case "in" -> predicates.add(path.in((Collection<?>) value));
                    case "greaterThan" -> predicates.add(cb.greaterThan(path.as(Comparable.class), (Comparable) value));
                    case "lessThan" -> predicates.add(cb. lessThanOrEqualTo(path.as(Comparable.class), (Comparable) value));
                    case "greaterThanEqual" -> predicates.add(cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) value));
                    case "lessThanEqual" -> predicates.add(cb.lessThan(path.as(Comparable.class), (Comparable) value));
                    default -> throw new UnsupportedOperationException("Op not supported: " + filter.operation());
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }     
    
}

