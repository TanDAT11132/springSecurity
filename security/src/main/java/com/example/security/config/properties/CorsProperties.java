package com.example.security.config.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "security.cors")
public class CorsProperties {
	private List<String> allowedOriginPatterns = new ArrayList<>(List.of("*"));
	private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	private List<String> allowedHeaders = new ArrayList<>(List.of("*"));
	private boolean allowCredentials = false;
}
