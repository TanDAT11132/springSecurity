package com.example.security.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.security.entity.User;

public interface UserRepo extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String username);

	@Query("""
			select distinct u from User u
			left join u.roles r
			where (:keyword is null or lower(u.username) like lower(concat('%', :keyword, '%')))
			  and (:checkrole is null or lower(u.checkrole) = lower(:checkrole))
			  and (:roleName is null or r.name = :roleName)
			""")
	Page<User> searchUsers(String keyword, String checkrole, String roleName, Pageable pageable);
}
