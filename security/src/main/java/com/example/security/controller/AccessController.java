package com.example.security.controller;

import java.time.LocalDateTime;
import java.util.Comparator;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.dto.ApiRespon;
import com.example.security.dto.CheckRoleRequest;
import com.example.security.dto.RoleCheckResponse;
import com.example.security.service.AccessControlService;
import com.example.security.service.UserSevice;

@RestController
@RequestMapping("api/access")
public class AccessController {

	private final AccessControlService accessControlService;
	private final UserSevice userSevice;

	public AccessController(AccessControlService accessControlService, UserSevice userSevice) {
		this.accessControlService = accessControlService;
		this.userSevice = userSevice;
	}

	@GetMapping("/me")
	public ResponseEntity<ApiRespon<?>> currentUser(Authentication authentication) {
		String username = authentication.getName();
		var user = userSevice.getByUsername(username);
		var authorities = userSevice.buildAuthorities(user).stream()
				.sorted(Comparator.naturalOrder())
				.toList();

		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"user info loaded",
				java.util.Map.of(
						"id", user.getId(),
						"username", user.getUsername(),
						"checkrole", user.getCheckrole(),
						"roles", user.getRoles().stream().map(role -> role.getName()).sorted().toList(),
						"authorities", authorities)));
	}

	@PostMapping("/check-role")
	public ResponseEntity<ApiRespon<RoleCheckResponse>> checkRole(
			Authentication authentication,
			@Valid @RequestBody CheckRoleRequest request) {
		RoleCheckResponse response = accessControlService.checkRole(authentication.getName(), request);
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"checkrole success",
				response));
	}
}
