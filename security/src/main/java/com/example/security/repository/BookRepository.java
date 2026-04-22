package com.example.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.security.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
	Optional<Book> findByIsbn(String isbn);
	boolean existsByIsbn(String isbn);
	boolean existsByIsbnAndIdNot(String isbn, Long id);
}
