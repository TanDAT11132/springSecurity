package com.example.security.until;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.util.StringUtils;

import com.example.security.security.TokenPayload;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

public final class JwtUntil {

	private JwtUntil() {
	}

	public static String generateToken(String secret, long expirationMinutes, String username, List<String> authorities,
			String checkrole) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(username)
				.claim("authorities", authorities)
				.claim("checkrole", checkrole)
				.issuedAt(java.util.Date.from(now))
				.expiration(java.util.Date.from(now.plusSeconds(expirationMinutes * 60)))
				.signWith(signingKey(secret))
				.compact();
	}

	public static TokenPayload parseToken(String secret, String token) {
		if (!StringUtils.hasText(token)) {
			throw new IllegalArgumentException("JWT khong duoc de trong");
		}
		try {
			Claims claims = Jwts.parser()
					.verifyWith(signingKey(secret))
					.build()
					.parseSignedClaims(token)
					.getPayload();

			TokenPayload payload = new TokenPayload();
			payload.setSub(claims.getSubject());
			payload.setCheckrole(claims.get("checkrole", String.class));
			payload.setAuthorities(toStringList(claims.get("authorities")));
			payload.setIat(claims.getIssuedAt() == null ? null : claims.getIssuedAt().toInstant().getEpochSecond());
			payload.setExp(claims.getExpiration() == null ? null : claims.getExpiration().toInstant().getEpochSecond());
			return payload;
		} catch (JwtException | IllegalArgumentException exception) {
			throw new IllegalArgumentException("JWT khong hop le hoac da het han", exception);
		}
	}

	private static SecretKey signingKey(String secret) {
		return Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}

	private static List<String> toStringList(Object claimValue) {
		if (!(claimValue instanceof Collection<?> values)) {
			return List.of();
		}
		return values.stream()
				.map(String::valueOf)
				.toList();
	}
}
