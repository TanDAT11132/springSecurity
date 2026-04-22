package com.example.security.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.security.dto.CheckRoleRequest;
import com.example.security.dto.RoleCheckResponse;
import com.example.security.entity.User;
import com.example.security.until.CheckRole;

@Service
public class AccessControlService {

	private final UserSevice userSevice;

	public AccessControlService(UserSevice userSevice) {
		this.userSevice = userSevice;
	}

	public RoleCheckResponse checkRole(String username, CheckRoleRequest request) {
		User user = userSevice.getByUsername(username);
		Set<String> authorities = userSevice.buildAuthorities(user);
		List<String> sortedAuthorities = authorities.stream().sorted(Comparator.naturalOrder()).toList();
		CheckRole checkRole = request.toCheckRole();

		if (StringUtils.hasText(request.getRequiredCheckrole())
				&& !request.getRequiredCheckrole().equalsIgnoreCase(user.getCheckrole())) {
			throw new AccessDeniedException("checkrole khong khop voi quyen hien tai");
		}

		if (!checkRole.matchesExtra(authorities)) {
			throw new AccessDeniedException("Nguoi dung chua du role/permission de nhan output cuoi");
		}

		if (checkRole.matchesExclude(authorities)) {
			throw new AccessDeniedException("Nguoi dung dang nam trong nhom quyen bi loai tru");
		}

		String finalOutput = "Final output duoc mo sau khi checkrole thanh cong cho user " + username
				+ " | extra=" + checkRole.normalizedExtra()
				+ " | exclude=" + checkRole.normalizedExclude();
		return new RoleCheckResponse(true, username, user.getCheckrole(), sortedAuthorities, finalOutput);
	}
}
