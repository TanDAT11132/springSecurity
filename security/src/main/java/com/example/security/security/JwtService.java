package com.example.security.security;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.security.config.properties.SecurityJwtProperties;
import com.example.security.until.JwtUntil;

@Service
public class JwtService {

	private final SecurityJwtProperties jwtProperties;

	public JwtService(SecurityJwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	public String generateToken(String username, List<String> authorities, String checkrole) {
		return JwtUntil.generateToken(
				jwtProperties.getSecret(),
				jwtProperties.getExpirationMinutes(),
				username,
				authorities,
				checkrole);
	}

	public TokenPayload parseToken(String token) {
		return JwtUntil.parseToken(jwtProperties.getSecret(), token);
	}
}
