package com.example.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.dto.BookResponse;
import com.example.security.entity.Book;
import com.example.security.repository.BookRepository;
import com.example.security.config.properties.BookCacheProperties;

@Service
public class BookCacheSyncService {

	private final BookRepository bookRepository;
	private final BookRedisCacheService bookRedisCacheService;
	private final BookCacheProperties bookCacheProperties;

	public BookCacheSyncService(
			BookRepository bookRepository,
			BookRedisCacheService bookRedisCacheService,
			BookCacheProperties bookCacheProperties) {
		this.bookRepository = bookRepository;
		this.bookRedisCacheService = bookRedisCacheService;
		this.bookCacheProperties = bookCacheProperties;
	}

	@Transactional(readOnly = true)
	public int syncAllBooksToRedis() {
		List<BookResponse> snapshots = new ArrayList<>();
		int pageNumber = 0;
		Page<Book> page;

		do {
			page = bookRepository.findAll(PageRequest.of(
					pageNumber,
					bookCacheProperties.getBatchSize(),
					Sort.by(Sort.Direction.ASC, "id")));
			page.getContent().stream()
					.map(this::toResponse)
					.forEach(snapshots::add);
			pageNumber++;
		} while (page.hasNext());

		bookRedisCacheService.saveSnapshot(snapshots);
		return snapshots.size();
	}

	private BookResponse toResponse(Book book) {
		return new BookResponse(
				book.getId(),
				book.getIsbn(),
				book.getTitle(),
				book.getAuthor(),
				book.getCategory(),
				book.getDescription(),
				book.getPublishedYear(),
				book.isAvailable(),
				book.getCreatedAt(),
				book.getUpdatedAt());
	}
}
