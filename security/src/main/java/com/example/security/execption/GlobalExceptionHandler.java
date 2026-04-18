package com.example.security.execption;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.security.dto.ApiRespon;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiRespon<?>> badcredential(){
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ApiRespon<>(LocalDateTime.now(),401,"sai ten dang nhap hoac mat khau",null));
	}
}
