package com.example.security.ratelimit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateLimitDecision {
	private boolean allowed;
	private int remainingRequests;
	private long retryAfterSeconds;
}
