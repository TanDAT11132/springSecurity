package com.example.security.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.security.dto.BookPageResponse;
import com.example.security.dto.BookRequest;
import com.example.security.dto.BookResponse;
import com.example.security.entity.Book;
import com.example.security.repository.BookRepository;
import com.example.security.repository.BookSpecification;

@Service
public class BookService {

	private final BookRepository bookRepository;
	private final BookRedisCacheService bookRedisCacheService;
	private final BookCacheSyncService bookCacheSyncService;

	public BookService(
			BookRepository bookRepository,
			BookRedisCacheService bookRedisCacheService,
			BookCacheSyncService bookCacheSyncService) {
		this.bookRepository = bookRepository;
		this.bookRedisCacheService = bookRedisCacheService;
		this.bookCacheSyncService = bookCacheSyncService;
	}

	@Transactional
	public BookResponse create(BookRequest request) {
		if (bookRepository.existsByIsbn(request.getIsbn().trim())) {
			throw new IllegalArgumentException("isbn da ton tai");
		}
		Book book = new Book();
		applyRequest(book, request);
		Book savedBook = bookRepository.save(book);
		bookRedisCacheService.evictSnapshot();
		return toResponse(savedBook);
	}

	@Transactional
	public BookResponse update(Long id, BookRequest request) {
		Book book = getEntity(id);
		if (bookRepository.existsByIsbnAndIdNot(request.getIsbn().trim(), id)) {
			throw new IllegalArgumentException("isbn da ton tai");
		}
		applyRequest(book, request);
		Book updatedBook = bookRepository.save(book);
		bookRedisCacheService.evictSnapshot();
		return toResponse(updatedBook);
	}

	@Transactional
	public void delete(Long id) {
		Book book = getEntity(id);
		bookRepository.delete(book);
		bookRedisCacheService.evictSnapshot();
	}

	@Transactional(readOnly = true)
	public BookResponse getById(Long id) {
		return toResponse(getEntity(id));
	}

	@Transactional(readOnly = true)
	public BookPageResponse list(String keyword, String author, String category, Boolean available, int page, int size) {
		int safePage = Math.max(0, page);
		int safeSize = Math.min(Math.max(1, size), 100);
		var snapshotOptional = bookRedisCacheService.getSnapshot();
		if (snapshotOptional.isPresent()) {
			List<BookResponse> filtered = snapshotOptional.get().getBooks().stream()
					.filter(book -> matches(book, keyword, author, category, available))
					.sorted(Comparator.comparing(BookResponse::getId))
					.toList();
			return toPageResponse(toPage(filtered, safePage, safeSize), true);
		}

		Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "id"));
		Page<Book> resultPage = bookRepository.findAll(
				BookSpecification.filter(keyword, author, category, available),
				pageable);
		return toPageResponse(resultPage.map(this::toResponse), false);
	}

	@Transactional
	public int syncToRedis() {
		return bookCacheSyncService.syncAllBooksToRedis();
	}

	private Book getEntity(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("khong tim thay book voi id " + id));
	}

	private void applyRequest(Book book, BookRequest request) {
		book.setIsbn(request.getIsbn().trim());
		book.setTitle(request.getTitle().trim());
		book.setAuthor(request.getAuthor().trim());
		book.setCategory(trimToNull(request.getCategory()));
		book.setDescription(trimToNull(request.getDescription()));
		book.setPublishedYear(request.getPublishedYear());
		book.setAvailable(request.isAvailable());
	}

	private String trimToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
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

	private boolean matches(BookResponse book, String keyword, String author, String category, Boolean available) {
		if (available != null && book.isAvailable() != available) {
			return false;
		}

		String keywordLower = normalize(keyword);
		if (keywordLower != null) {
			boolean hit = Stream.of(book.getTitle(), book.getIsbn(), book.getAuthor())
					.filter(StringUtils::hasText)
					.map(this::normalizeValue)
					.anyMatch(value -> value.contains(keywordLower));
			if (!hit) {
				return false;
			}
		}

		String authorLower = normalize(author);
		if (authorLower != null && !normalizeValue(book.getAuthor()).contains(authorLower)) {
			return false;
		}

		String categoryLower = normalize(category);
		if (categoryLower != null && !normalizeValue(book.getCategory()).contains(categoryLower)) {
			return false;
		}
		return true;
	}

	private String normalize(String value) {
		return StringUtils.hasText(value) ? value.trim().toLowerCase(Locale.ROOT) : null;
	}

	private String normalizeValue(String value) {
		return value == null ? "" : value.toLowerCase(Locale.ROOT);
	}

	private Page<BookResponse> toPage(List<BookResponse> books, int page, int size) {
		int start = Math.min(page * size, books.size());
		int end = Math.min(start + size, books.size());
		return new PageImpl<>(books.subList(start, end), PageRequest.of(page, size), books.size());
	}

	private BookPageResponse toPageResponse(Page<BookResponse> page, boolean fromRedis) {
		return new BookPageResponse(
				page.getContent(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				fromRedis);
	}
}
