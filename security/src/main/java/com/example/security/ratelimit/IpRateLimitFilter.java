package com.example.security.ratelimit;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.security.config.properties.RateLimitProperties;
import com.example.security.dto.ApiRespon;
import com.example.security.until.JsonUntil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class IpRateLimitFilter extends OncePerRequestFilter {

	private final RateLimiterService rateLimiterService;
	private final RateLimitProperties rateLimitProperties;

	public IpRateLimitFilter(RateLimiterService rateLimiterService, RateLimitProperties rateLimitProperties) {
		this.rateLimiterService = rateLimiterService;
		this.rateLimitProperties = rateLimitProperties;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		return HttpMethod.OPTIONS.matches(request.getMethod())
				|| !path.startsWith("/api/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String path = request.getServletPath();
		String clientIp = ClientIpResolver.resolve(request);
		boolean authEndpoint = path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register");
		RateLimitProperties.Rule rule = authEndpoint ? rateLimitProperties.getAuth() : rateLimitProperties.getApi();
		String bucketKey = (authEndpoint ? "AUTH" : "API") + ":" + clientIp;

		RateLimitDecision decision = rateLimiterService.evaluate(bucketKey, rule);
		response.setHeader("X-RateLimit-Remaining", String.valueOf(decision.getRemainingRequests()));
		if (!decision.isAllowed()) {
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			response.setContentType("application/json");
			response.setHeader("Retry-After", String.valueOf(decision.getRetryAfterSeconds()));
			response.getWriter().write(JsonUntil.toJon(
					new ApiRespon<>(
							LocalDateTime.now(),
							429,
							"Qua nhieu request tu IP " + clientIp + ". Vui long thu lai sau "
									+ decision.getRetryAfterSeconds() + " giay",
							null)));
			return;
		}

		filterChain.doFilter(request, response);
	}
}
