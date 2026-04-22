package com.example.security.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleSummaryResponse {
	private String name;
	private List<String> permissions;
}
