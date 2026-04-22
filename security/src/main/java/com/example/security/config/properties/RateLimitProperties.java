package com.example.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "security.rate-limit")
public class RateLimitProperties {
	private Rule auth = new Rule(10, 1);
	private Rule api = new Rule(120, 1);

	@Data
	public static class Rule {
		private int maxRequests;
		private long windowMinutes;

		public Rule() {
		}

		public Rule(int maxRequests, long windowMinutes) {
			this.maxRequests = maxRequests;
			this.windowMinutes = windowMinutes;
		}
	}
}
