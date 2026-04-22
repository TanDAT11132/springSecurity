package com.example.security.ratelimit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.security.config.properties.RateLimitProperties;

class InMemoryRateLimiterServiceTests {

	@Test
	void shouldBlockAfterConfiguredLimit() {
		InMemoryRateLimiterService service = new InMemoryRateLimiterService();
		RateLimitProperties.Rule rule = new RateLimitProperties.Rule(2, 1);

		assertTrue(service.evaluate("AUTH:127.0.0.1", rule).isAllowed());
		assertTrue(service.evaluate("AUTH:127.0.0.1", rule).isAllowed());
		assertFalse(service.evaluate("AUTH:127.0.0.1", rule).isAllowed());
	}
}
