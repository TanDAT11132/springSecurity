package com.example.security.security;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TokenPayload {
	private String sub;
	private List<String> authorities = new ArrayList<>();
	private String checkrole;
	private Long iat;
	private Long exp;
}
