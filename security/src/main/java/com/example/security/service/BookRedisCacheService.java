package com.example.security.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.security.dto.BookCacheSnapshot;
import com.example.security.dto.BookResponse;
import com.example.security.config.properties.BookCacheProperties;
import com.example.security.until.JsonUntil;

@Service
public class BookRedisCacheService {

	private final StringRedisTemplate stringRedisTemplate;
	private final BookCacheProperties bookCacheProperties;

	public BookRedisCacheService(StringRedisTemplate stringRedisTemplate, BookCacheProperties bookCacheProperties) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.bookCacheProperties = bookCacheProperties;
	}

	public Optional<BookCacheSnapshot> getSnapshot() {
		if (!bookCacheProperties.isEnabled()) {
			return Optional.empty();
		}
		try {
			String json = stringRedisTemplate.opsForValue().get(bookCacheProperties.getRedisKey());
			if (json == null || json.isBlank()) {
				return Optional.empty();
			}
			return Optional.of(JsonUntil.formJson(json, BookCacheSnapshot.class));
		} catch (Exception exception) {
			return Optional.empty();
		}
	}

	public void saveSnapshot(java.util.List<BookResponse> books) {
		if (!bookCacheProperties.isEnabled()) {
			return;
		}
		try {
			BookCacheSnapshot snapshot = new BookCacheSnapshot(LocalDateTime.now(), books);
			stringRedisTemplate.opsForValue().set(bookCacheProperties.getRedisKey(), JsonUntil.toJon(snapshot));
		} catch (Exception exception) {
			// Redis co the tam thoi khong san sang, khong lam app fail.
		}
	}

	public void evictSnapshot() {
		if (!bookCacheProperties.isEnabled()) {
			return;
		}
		try {
			stringRedisTemplate.delete(bookCacheProperties.getRedisKey());
		} catch (Exception exception) {
			// Ignore de ghi DB van thanh cong.
		}
	}
}
