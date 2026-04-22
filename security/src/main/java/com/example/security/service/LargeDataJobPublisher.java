package com.example.security.service;

import com.example.security.dto.BookBulkJobRequest;

public interface LargeDataJobPublisher {
	void publish(BookBulkJobRequest request);
}
