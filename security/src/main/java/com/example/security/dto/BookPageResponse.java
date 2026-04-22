package com.example.security.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookPageResponse {
	private List<BookResponse> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean fromRedis;
}
