package com.example.security.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.dto.ApiRespon;
import com.example.security.dto.AuthenRequset;
import com.example.security.service.UserSevice;

@RestController
@RequestMapping("api/auth")
public class AuthenContronleer {
	@Autowired
	private UserSevice userservice;
	private final AuthenticationManager authenticationManager;

	public AuthenContronleer(AuthenticationManager authenticationManager) {
	    this.authenticationManager = authenticationManager;
	}
	@PostMapping("/login")
	public ResponseEntity<ApiRespon<?>> login(@RequestBody AuthenRequset request){
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
				);
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200, 
				"login success",
				Map.of("username",request.getUsername())
				)
			);
	}
	@PostMapping("/register")
	public ResponseEntity<ApiRespon<?>> register(@RequestBody AuthenRequset request){
		System.out.println(request);
		userservice.create(request.getUsername(),request.getPassword());
		
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200, 
				"login success",
				Map.of("username",request.getUsername())
				)
			);

	}
}
