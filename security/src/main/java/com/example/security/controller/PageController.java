package com.example.security.controller;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/")
	public void home(Authentication authentication, HttpServletResponse response) throws IOException {
		if (authentication != null
				&& authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			response.sendRedirect("/index.html");
			return;
		}
		response.sendRedirect("/login.html");
	}
}
