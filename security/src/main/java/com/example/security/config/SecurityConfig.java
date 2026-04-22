package com.example.security.config;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.security.dto.ApiRespon;
import com.example.security.config.properties.CorsProperties;
import com.example.security.config.properties.BookCacheProperties;
import com.example.security.config.properties.RateLimitProperties;
import com.example.security.config.properties.RabbitPipelineProperties;
import com.example.security.config.properties.SecurityJwtProperties;
import com.example.security.ratelimit.IpRateLimitFilter;
import com.example.security.security.JwtAuthenticationFilter;
import com.example.security.service.UserSevice;
import com.example.security.until.JsonUntil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({
		SecurityJwtProperties.class,
		RateLimitProperties.class,
		CorsProperties.class,
		BookCacheProperties.class,
		RabbitPipelineProperties.class
})
public class SecurityConfig{

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final IpRateLimitFilter ipRateLimitFilter;
	private final CorsProperties corsProperties;

	SecurityConfig(
			JwtAuthenticationFilter jwtAuthenticationFilter,
			IpRateLimitFilter ipRateLimitFilter,
			CorsProperties corsProperties) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.ipRateLimitFilter = ipRateLimitFilter;
		this.corsProperties = corsProperties;
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,UserSevice userservice)throws Exception {
		http.cors(cors -> cors.configurationSource(configurationSource()))
		.csrf(csrf->csrf.disable())
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/login.html", "/api/auth/login", "/api/auth/register", "/api/auth/logout", "/error").permitAll()
				.anyRequest().authenticated())
		.formLogin(form -> form.disable())
		.exceptionHandling(exception -> exception
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setContentType("application/json");
					response.getWriter().write(JsonUntil.toJon(
							new ApiRespon<>(LocalDateTime.now(), 401, "Ban can dang nhap bang JWT hop le", null)));
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(HttpStatus.FORBIDDEN.value());
					response.setContentType("application/json");
					response.getWriter().write(JsonUntil.toJon(
							new ApiRespon<>(LocalDateTime.now(), 403, accessDeniedException.getMessage(), null)));
				}))
		.userDetailsService(userservice)
		.addFilterBefore(ipRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}
	@Bean
	public CorsConfigurationSource configurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedHeaders(corsProperties.getAllowedHeaders());
		config.setAllowedMethods(corsProperties.getAllowedMethods());
		config.setAllowedOriginPatterns(corsProperties.getAllowedOriginPatterns());
		config.setAllowCredentials(corsProperties.isAllowCredentials());
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
}
