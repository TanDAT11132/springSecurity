package com.example.security.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security.dto.AuthResponse;
import com.example.security.dto.AuthenRequset;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.RoleRepo;
import com.example.security.repository.UserRepo;
import com.example.security.security.JwtService;
import com.example.security.config.properties.SecurityJwtProperties;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserSevice userSevice;
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final JwtService jwtService;
	private final SecurityJwtProperties jwtProperties;

	public AuthService(
			AuthenticationManager authenticationManager,
			UserSevice userSevice,
			UserRepo userRepo,
			RoleRepo roleRepo,
			JwtService jwtService,
			SecurityJwtProperties jwtProperties) {
		this.authenticationManager = authenticationManager;
		this.userSevice = userSevice;
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.jwtService = jwtService;
		this.jwtProperties = jwtProperties;
	}

	@Transactional
	public AuthResponse register(AuthenRequset request) {
		if (userRepo.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Ten dang nhap da ton tai");
		}

		User user = userSevice.create(request.getUsername(), request.getPassword());
		Role defaultRole = roleRepo.findByName("USER1")
				.orElseThrow(() -> new IllegalStateException("Khong tim thay role mac dinh USER1"));
		user.getRoles().add(defaultRole);
		userRepo.save(user);

		return toAuthResponse(user);
	}

	@Transactional
	public AuthResponse login(AuthenRequset request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		User user = userSevice.getByUsername(request.getUsername());
		return toAuthResponse(user);
	}

	private AuthResponse toAuthResponse(User user) {
		List<String> authorities = userSevice.buildAuthorities(user).stream()
				.sorted(Comparator.naturalOrder())
				.toList();
		String token = jwtService.generateToken(user.getUsername(), authorities, user.getCheckrole());
		return new AuthResponse(user.getUsername(), token, "Bearer", user.getCheckrole(), authorities);
	}

	public long getExpirationMinutes() {
		return jwtProperties.getExpirationMinutes();
	}
}
