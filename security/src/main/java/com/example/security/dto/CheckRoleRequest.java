package com.example.security.dto;

import java.util.List;

import com.example.security.until.CheckRole;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckRoleRequest {
	private List<String> extra;
	private List<String> exclude;
	private List<String> requiredAuthorities;
	private List<String> excludedAuthorities;
	@Size(max = 50, message = "requiredCheckrole toi da 50 ky tu")
	private String requiredCheckrole;

	public CheckRole toCheckRole() {
		CheckRole checkRole = new CheckRole();
		checkRole.setExtra(extra != null ? extra : requiredAuthorities);
		checkRole.setExclude(exclude != null ? exclude : excludedAuthorities);
		return checkRole;
	}
}
