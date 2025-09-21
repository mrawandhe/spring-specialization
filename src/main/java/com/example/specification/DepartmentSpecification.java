package com.example.specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.entity.Department;

public class DepartmentSpecification {
	
	public static Specification<Department> hasLocation(String location){
		return (root, query, cb) -> {
			if(!StringUtils.hasText(location)) return cb.conjunction();
			return cb.equal(root.get("location"), location);
		};
	}
	
	public static Specification<Department> createdByLike(String createdBy){
		return (root, query, cb) -> {
			if(!StringUtils.hasText(createdBy)) return cb.conjunction();
			String text = "%" + createdBy + "%";
			return cb.like(cb.lower(root.join("createdBy").get("username")), text.toLowerCase());
		};
	}

	public static Specification<Department> budgetGreaterThen(BigDecimal budget){
		return (root, query, cb) -> {
			if(Objects.isNull(budget)) return cb.conjunction();
			return cb.greaterThan(root.<BigDecimal>get("budget"), budget);
		};
	}
	
	public static Specification<Department> createdBetweenDates(LocalDateTime startDate, LocalDateTime endDate){
		return (root, query, cb) -> {
			if(Objects.isNull(startDate) || Objects.isNull(endDate)) return cb.conjunction();
			return cb.between(root.<LocalDateTime>get("createdAt"), startDate, endDate);
		};
	}
	
	
}
