package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;


@Builder
public record DepartmentDto(Long id,
							String departmentName,
							String location,
							String manager,
							BigDecimal budget,
							String createdBy,
							LocalDateTime createdAt) {
}
