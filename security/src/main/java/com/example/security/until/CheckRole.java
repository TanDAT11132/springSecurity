package com.example.security.until;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class CheckRole {
	private List<String> extra;
	private List<String> exclude;

	public List<String> normalizedExtra() {
		return normalize(extra);
	}

	public List<String> normalizedExclude() {
		return normalize(exclude);
	}

	public boolean matchesExtra(Set<String> authorities) {
		return normalizedExtra().stream().allMatch(authorities::contains);
	}

	public boolean matchesExclude(Set<String> authorities) {
		return normalizedExclude().stream().anyMatch(authorities::contains);
	}

	private List<String> normalize(List<String> values) {
		if (values == null) {
			return List.of();
		}
		return values.stream()
				.filter(StringUtils::hasText)
				.map(String::trim)
				.map(String::toUpperCase)
				.distinct()
				.collect(Collectors.toList());
	}
}
