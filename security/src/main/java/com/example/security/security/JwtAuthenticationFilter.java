package com.example.security.security;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		String token = resolveToken(authHeader, request.getCookies());
		if (!StringUtils.hasText(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			TokenPayload payload = jwtService.parseToken(token);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					payload.getSub(),
					null,
					payload.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (IllegalArgumentException exception) {
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(String authHeader, Cookie[] cookies) {
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if ("security.jwt.token".equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
