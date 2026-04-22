package com.example.security.controller;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.dto.AdminUserPageResponse;
import com.example.security.dto.AdminUserResponse;
import com.example.security.dto.AdminUserUpdateRequest;
import com.example.security.dto.ApiRespon;
import com.example.security.dto.RoleSummaryResponse;
import com.example.security.service.AdminService;

@RestController
@RequestMapping("api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/users")
	public ResponseEntity<ApiRespon<AdminUserPageResponse>> searchUsers(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String role,
			@RequestParam(required = false) String checkrole,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"users loaded",
				adminService.searchUsers(keyword, role, checkrole, page, size)));
	}

	@GetMapping("/roles")
	public ResponseEntity<ApiRespon<List<RoleSummaryResponse>>> listRoles() {
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"roles loaded",
				adminService.listRoles()));
	}

	@PutMapping("/users/{id}/access")
	public ResponseEntity<ApiRespon<AdminUserResponse>> updateUserAccess(
			@PathVariable int id,
			@Valid @RequestBody AdminUserUpdateRequest request) {
		return ResponseEntity.ok(new ApiRespon<>(
				LocalDateTime.now(),
				200,
				"user access updated",
				adminService.updateUserAccess(id, request)));
	}
}
