package com.example.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityJwtProperties {
	private String secret;
	private long expirationMinutes = 120;
}
