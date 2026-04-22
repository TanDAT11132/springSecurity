package com.example.security.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookRequest {
	@NotBlank(message = "isbn khong duoc de trong")
	@Size(max = 30, message = "isbn toi da 30 ky tu")
	private String isbn;

	@NotBlank(message = "title khong duoc de trong")
	@Size(max = 255, message = "title toi da 255 ky tu")
	private String title;

	@NotBlank(message = "author khong duoc de trong")
	@Size(max = 150, message = "author toi da 150 ky tu")
	private String author;

	@Size(max = 100, message = "category toi da 100 ky tu")
	private String category;

	@Size(max = 1000, message = "description toi da 1000 ky tu")
	private String description;

	@Min(value = 0, message = "publishedYear khong hop le")
	@Max(value = 3000, message = "publishedYear khong hop le")
	private Integer publishedYear;

	private boolean available = true;
}
