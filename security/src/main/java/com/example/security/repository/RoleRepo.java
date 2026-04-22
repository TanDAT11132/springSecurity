package com.example.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(String name);
	List<Role> findByNameIn(List<String> names);
}
