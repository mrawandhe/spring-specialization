package com.example.dto;

import lombok.Builder;

@Builder
public record PageMetadata(int pageNumber,
						   int pageSize,
						   int totalPages,
						   int noOfElements,
						   int totalElements) {
}
