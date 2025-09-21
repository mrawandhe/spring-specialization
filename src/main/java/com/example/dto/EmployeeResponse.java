package com.example.dto;

import java.util.List;

import lombok.Builder;


@Builder
public record EmployeeResponse(List<EmployeeDto> data,
							   PageMetadata pageMetadata) {
}
