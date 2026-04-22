package com.example.security.until;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class JwtUntilTests {

	private static final String VALID_SECRET = "test-secret-key-12345678901234567890";
	private static final String OTHER_SECRET = "other-secret-key-1234567890123456789";

	@Test
	void shouldGenerateAndParseToken() {
		String token = JwtUntil.generateToken(
				VALID_SECRET,
				30,
				"alice",
				List.of("ROLE_USER1", "PER_1"),
				"ACTIVE");

		var payload = JwtUntil.parseToken(VALID_SECRET, token);

		assertEquals("alice", payload.getSub());
		assertEquals("ACTIVE", payload.getCheckrole());
		assertEquals(List.of("ROLE_USER1", "PER_1"), payload.getAuthorities());
	}

	@Test
	void shouldRejectInvalidSignature() {
		String token = JwtUntil.generateToken(
				VALID_SECRET,
				30,
				"alice",
				List.of("ROLE_USER1"),
				"ACTIVE");

		assertThrows(IllegalArgumentException.class, () -> JwtUntil.parseToken(OTHER_SECRET, token));
	}

	@Test
	void shouldEvaluateExtraAndExcludeRules() {
		CheckRole checkRole = new CheckRole();
		checkRole.setExtra(List.of("ROLE_USER1", "PER_1"));
		checkRole.setExclude(List.of("ROLE_ADMIN"));

		Set<String> authorities = Set.of("ROLE_USER1", "PER_1", "PER_2");

		assertTrue(checkRole.matchesExtra(authorities));
		assertFalse(checkRole.matchesExclude(authorities));
	}
}
