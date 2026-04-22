package com.example.security.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.security.entity.Book;

public final class BookSpecification {

	private BookSpecification() {
	}

	public static Specification<Book> filter(String keyword, String author, String category, Boolean available) {
		return (root, query, criteriaBuilder) -> {
			var predicate = criteriaBuilder.conjunction();

			if (StringUtils.hasText(keyword)) {
				String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
				predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likeKeyword),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), likeKeyword),
						criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), likeKeyword)));
			}

			if (StringUtils.hasText(author)) {
				predicate = criteriaBuilder.and(
						predicate,
						criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.trim().toLowerCase() + "%"));
			}

			if (StringUtils.hasText(category)) {
				predicate = criteriaBuilder.and(
						predicate,
						criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), "%" + category.trim().toLowerCase() + "%"));
			}

			if (available != null) {
				predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("available"), available));
			}

			return predicate;
		};
	}
}
