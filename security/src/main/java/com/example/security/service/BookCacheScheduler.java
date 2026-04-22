package com.example.security.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.security.config.properties.BookCacheProperties;

@Component
public class BookCacheScheduler {

	private final BookCacheSyncService bookCacheSyncService;
	private final BookCacheProperties bookCacheProperties;

	public BookCacheScheduler(BookCacheSyncService bookCacheSyncService, BookCacheProperties bookCacheProperties) {
		this.bookCacheSyncService = bookCacheSyncService;
		this.bookCacheProperties = bookCacheProperties;
	}

	@Scheduled(
			fixedDelayString = "${book.cache.sync-interval-ms:300000}",
			initialDelayString = "${book.cache.initial-delay-ms:20000}")
	public void syncBooks() {
		if (!bookCacheProperties.isEnabled()) {
			return;
		}
		bookCacheSyncService.syncAllBooksToRedis();
	}
}
