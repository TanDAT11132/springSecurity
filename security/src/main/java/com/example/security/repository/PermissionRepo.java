package com.example.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.entity.Permission;

public interface PermissionRepo extends JpaRepository<Permission, Integer> {
	Optional<Permission> findByName(String name);
}
