package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;


@Builder
public record EmployeeDto(Long id,
						 String firstName,
						 String lastName,
						 String email,
						 LocalDate hireDate,
						 BigDecimal salary,
						 Long departmentId,
						 String departmentName,
						 Long createdById,
						 String createdBy,
						 LocalDateTime createdAt) {
}
