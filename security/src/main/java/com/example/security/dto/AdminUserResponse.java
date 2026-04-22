package com.example.security.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminUserResponse {
	private int id;
	private String username;
	private String checkrole;
	private List<String> roles;
	private List<String> authorities;
}
