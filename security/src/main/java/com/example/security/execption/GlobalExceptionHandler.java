package com.example.security.execption;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiRespon<?>> illegalArgument(IllegalArgumentException exception){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiRespon<>(LocalDateTime.now(),400,exception.getMessage(),null));
	}
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiRespon<?>> accessDenied(AccessDeniedException exception){
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ApiRespon<>(LocalDateTime.now(),403,exception.getMessage(),null));
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiRespon<?>> validation(MethodArgumentNotValidException exception){
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining("; "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiRespon<>(LocalDateTime.now(),400,message,null));
	}
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiRespon<?>> illegalState(IllegalStateException exception){
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiRespon<>(LocalDateTime.now(),503,exception.getMessage(),null));
	}
}
