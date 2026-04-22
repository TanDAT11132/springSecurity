package com.example.security.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.dto.BookBulkJobRequest;
import com.example.security.dto.BookPageResponse;
import com.example.security.dto.BookRequest;
import com.example.security.dto.BookResponse;
import com.example.security.service.BookService;
import com.example.security.dto.ApiRespon;
import com.example.security.service.LargeDataJobPublisher;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/books")
public class BookController {

	private final BookService bookService;
	private final LargeDataJobPublisher largeDataJobPublisher;

	public BookController(BookService bookService, LargeDataJobPublisher largeDataJobPublisher) {
		this.bookService = bookService;
		this.largeDataJobPublisher = largeDataJobPublisher;
	}

	@GetMapping
	public ResponseEntity<ApiRespon<BookPageResponse>> list(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) Boolean available,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"books loaded",
				bookService.list(keyword, author, category, available, page, size)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiRespon<BookResponse>> getById(@PathVariable Long id) {
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"book loaded",
				bookService.getById(id)));
	}

	@PostMapping
	public ResponseEntity<ApiRespon<BookResponse>> create(@Valid @RequestBody BookRequest request) {
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"book created",
				bookService.create(request)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiRespon<BookResponse>> update(
			@PathVariable Long id,
			@Valid @RequestBody BookRequest request) {
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"book updated",
				bookService.update(id, request)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiRespon<Map<String, Object>>> delete(@PathVariable Long id) {
		bookService.delete(id);
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"book deleted",
				Map.of("deletedId", id)));
	}

	@PostMapping("/cache/sync")
	public ResponseEntity<ApiRespon<Map<String, Object>>> syncCache() {
		int totalBooks = bookService.syncToRedis();
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"book cache synced",
				Map.of("totalBooks", totalBooks)));
	}

	@PostMapping("/jobs/bulk-sync")
	public ResponseEntity<ApiRespon<Map<String, Object>>> pushLargeDataJob(Authentication authentication) {
		BookBulkJobRequest request = new BookBulkJobRequest(
				"BOOK_BULK_SYNC",
				authentication.getName(),
				System.currentTimeMillis());
		largeDataJobPublisher.publish(request);
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"bulk data job published",
				Map.of(
						"jobType", request.getJobType(),
						"requestedBy", request.getRequestedBy())));
	}
}
