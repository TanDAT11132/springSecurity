package com.example.security.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookResponse {
	private Long id;
	private String isbn;
	private String title;
	private String author;
	private String category;
	private String description;
	private Integer publishedYear;
	private boolean available;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
