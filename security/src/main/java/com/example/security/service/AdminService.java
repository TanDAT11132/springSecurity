package com.example.security.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.security.dto.AdminUserPageResponse;
import com.example.security.dto.AdminUserResponse;
import com.example.security.dto.AdminUserUpdateRequest;
import com.example.security.dto.RoleSummaryResponse;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.RoleRepo;
import com.example.security.repository.UserRepo;

@Service
public class AdminService {

	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final UserSevice userSevice;

	public AdminService(UserRepo userRepo, RoleRepo roleRepo, UserSevice userSevice) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.userSevice = userSevice;
	}

	@Transactional(readOnly = true)
	public AdminUserPageResponse searchUsers(String keyword, String roleName, String checkrole, int page, int size) {
		int safePage = Math.max(0, page);
		int safeSize = Math.min(Math.max(1, size), 50);
		var result = userRepo.searchUsers(
				normalize(keyword),
				normalize(checkrole),
				normalizeRole(roleName),
				PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "id")));
		return new AdminUserPageResponse(
				result.getContent().stream().map(this::toAdminUserResponse).toList(),
				result.getNumber(),
				result.getSize(),
				result.getTotalElements(),
				result.getTotalPages());
	}

	@Transactional(readOnly = true)
	public List<RoleSummaryResponse> listRoles() {
		return roleRepo.findAll().stream()
				.sorted(Comparator.comparing(Role::getName))
				.map(role -> new RoleSummaryResponse(
						role.getName(),
						role.getPermissions().stream()
								.map(permission -> permission.getName())
								.sorted()
								.toList()))
				.toList();
	}

	@Transactional
	public AdminUserResponse updateUserAccess(int userId, AdminUserUpdateRequest request) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("khong tim thay user voi id " + userId));
		List<String> normalizedRoleNames = request.getRoleNames().stream()
				.filter(StringUtils::hasText)
				.map(String::trim)
				.distinct()
				.toList();
		List<Role> roles = roleRepo.findByNameIn(normalizedRoleNames);
		if (roles.size() != normalizedRoleNames.size()) {
			throw new IllegalArgumentException("mot hoac nhieu role khong ton tai trong he thong");
		}

		user.getRoles().clear();
		user.getRoles().addAll(roles);
		user.setCheckrole(request.getCheckrole().trim());
		return toAdminUserResponse(userRepo.save(user));
	}

	private AdminUserResponse toAdminUserResponse(User user) {
		List<String> roles = user.getRoles().stream()
				.map(Role::getName)
				.sorted()
				.toList();
		List<String> authorities = userSevice.buildAuthorities(user).stream()
				.sorted()
				.toList();
		return new AdminUserResponse(user.getId(), user.getUsername(), user.getCheckrole(), roles, authorities);
	}

	private String normalize(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}

	private String normalizeRole(String value) {
		return StringUtils.hasText(value) ? value.trim().toUpperCase() : null;
	}
}
