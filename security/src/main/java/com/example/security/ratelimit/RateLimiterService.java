package com.example.security.ratelimit;

import com.example.security.config.properties.RateLimitProperties;

public interface RateLimiterService {
	RateLimitDecision evaluate(String bucketKey, RateLimitProperties.Rule rule);
}
