package com.example.security.controller;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.dto.ApiRespon;
import com.example.security.dto.AuthResponse;
import com.example.security.dto.AuthenRequset;
import com.example.security.service.AuthService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<ApiRespon<AuthResponse>> login(
			@Valid @RequestBody AuthenRequset request,
			HttpServletResponse response) {
		AuthResponse authResponse = authService.login(request);
		writeTokenCookie(response, authResponse.getToken(), authService.getExpirationMinutes());
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"login success",
				authResponse));
	}

	@PostMapping("/register")
	public ResponseEntity<ApiRespon<AuthResponse>> register(
			@Valid @RequestBody AuthenRequset request,
			HttpServletResponse response) {
		AuthResponse authResponse = authService.register(request);
		writeTokenCookie(response, authResponse.getToken(), authService.getExpirationMinutes());
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"register success",
				authResponse));
	}

	@GetMapping("/validate")
	public ResponseEntity<ApiRespon<Map<String, Object>>> validate(Authentication authentication) {
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"jwt valid",
				Map.of(
						"authenticated", true,
						"username", authentication.getName())));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiRespon<Map<String, Object>>> logout(HttpServletResponse response) {
		response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("security.jwt.token", "")
				.httpOnly(true)
				.path("/")
				.maxAge(Duration.ZERO)
				.sameSite("Lax")
				.build()
				.toString());
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"logout success",
				Map.of("loggedOut", true)));
	}

	private void writeTokenCookie(HttpServletResponse response, String token, long expirationMinutes) {
		ResponseCookie cookie = ResponseCookie.from("security.jwt.token", token)
				.httpOnly(true)
				.path("/")
				.maxAge(Duration.ofMinutes(expirationMinutes))
				.sameSite("Lax")
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
}
