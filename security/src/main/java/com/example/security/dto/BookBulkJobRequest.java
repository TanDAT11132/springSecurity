package com.example.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookBulkJobRequest {
	private String jobType;
	private String requestedBy;
	private long requestedAtEpochMs;
}
