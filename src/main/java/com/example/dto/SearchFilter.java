package com.example.dto;
 
public record SearchFilter(String columnName,
						   String value,
						   String operation) {
}
