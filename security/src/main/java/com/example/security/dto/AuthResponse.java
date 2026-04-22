package com.example.security.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
	private String username;
	private String token;
	private String tokenType;
	private String checkrole;
	private List<String> authorities;
}
