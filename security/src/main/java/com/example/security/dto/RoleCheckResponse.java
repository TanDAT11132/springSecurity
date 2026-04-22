package com.example.security.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleCheckResponse {
	private boolean allowed;
	private String username;
	private String checkrole;
	private List<String> authorities;
	private String finalOutput;
}
