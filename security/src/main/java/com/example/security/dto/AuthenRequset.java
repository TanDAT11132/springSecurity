package com.example.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenRequset {
	@NotBlank(message = "username khong duoc de trong")
	@Size(min = 3, max = 50, message = "username phai tu 3 den 50 ky tu")
	String username;

	@NotBlank(message = "password khong duoc de trong")
	@Size(min = 6, max = 255, message = "password phai tu 6 ky tu tro len")
	String password;
}
