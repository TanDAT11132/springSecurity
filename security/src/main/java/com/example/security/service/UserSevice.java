package com.example.security.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.security.entity.Permission;
import com.example.security.entity.Role;
import com.example.security.entity.User;
import com.example.security.repository.UserRepo;

@Service
public class UserSevice implements UserDetailsService {
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	public UserSevice(UserRepo userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		User user = userRepo.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));

		Set<String> authorities = buildAuthorities(user);

		Set<GrantedAuthority> grantedAuthorities = authorities.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(java.util.stream.Collectors.toSet());
		return org.springframework.security.core.userdetails.User.builder()
				.username(username)
				.password(user.getPassword())
				.authorities(grantedAuthorities)
				.build();
	}
	public Set<String> buildAuthorities(User user){
		Set<String> authorities = new LinkedHashSet<>();
		for (Role role : user.getRoles()) {
			authorities.add("ROLE_" + role.getName());
			for (Permission permission : role.getPermissions()) {
				authorities.add(permission.getName());
			}
		}
		return authorities;
	}
	public User create(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setCheckrole("ACTIVE");
		return userRepo.save(user);
	}

	public User getByUsername(String username) {
		return userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}
	 
}
