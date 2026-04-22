package com.example.security.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AdminUserUpdateRequest {
	@NotEmpty(message = "roleNames khong duoc de trong")
	private List<String> roleNames;

	@NotBlank(message = "checkrole khong duoc de trong")
	private String checkrole;
}
