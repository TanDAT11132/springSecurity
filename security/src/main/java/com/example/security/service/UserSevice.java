package com.example.security.service;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.security.entity.User;
import com.example.security.entity.repository.UserRepo;

@Service
public class UserSevice implements UserDetailsService {
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
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
				.authorities("user")
				.build();
	}
	public Set<String> buildAuthorities(User user){
		
	}
	public void create(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		userRepo.save(user);
	}
	 
}
