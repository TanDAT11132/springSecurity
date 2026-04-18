package com.example.security.until;

import java.util.List;

import lombok.Data;

@Data
public class CheckRole {
	private List<String> extra;
	private List<String> exclude;
}
