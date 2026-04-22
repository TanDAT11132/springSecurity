package com.example.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "book.cache")
public class BookCacheProperties {
	private boolean enabled = true;
	private String redisKey = "books:snapshot";
	private int batchSize = 200;
	private long syncIntervalMs = 300000;
	private long initialDelayMs = 20000;
}
