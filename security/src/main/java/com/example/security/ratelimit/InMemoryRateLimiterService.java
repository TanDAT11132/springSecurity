package com.example.security.ratelimit;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.security.config.properties.RateLimitProperties;

@Service
public class InMemoryRateLimiterService implements RateLimiterService {

	private final Map<String, Deque<Instant>> buckets = new ConcurrentHashMap<>();

	@Override
	public RateLimitDecision evaluate(String bucketKey, RateLimitProperties.Rule rule) {
		Deque<Instant> requests = buckets.computeIfAbsent(bucketKey, key -> new ArrayDeque<>());
		Instant now = Instant.now();
		Instant windowStart = now.minus(Duration.ofMinutes(rule.getWindowMinutes()));

		synchronized (requests) {
			while (!requests.isEmpty() && requests.peekFirst().isBefore(windowStart)) {
				requests.pollFirst();
			}

			if (requests.size() >= rule.getMaxRequests()) {
				Instant oldestRequest = requests.peekFirst();
				long retryAfterSeconds = oldestRequest == null
						? 0
						: Math.max(1, Duration.between(now, oldestRequest.plus(Duration.ofMinutes(rule.getWindowMinutes())))
								.getSeconds());
				return new RateLimitDecision(false, 0, retryAfterSeconds);
			}

			requests.addLast(now);
			int remainingRequests = Math.max(0, rule.getMaxRequests() - requests.size());
			return new RateLimitDecision(true, remainingRequests, 0);
		}
	}
}
