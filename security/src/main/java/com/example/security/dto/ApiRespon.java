package com.example.security.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ApiRespon<T> {
	private LocalDateTime timestamp;
	private int status;
	private String message;
	private T payload;
	
}
