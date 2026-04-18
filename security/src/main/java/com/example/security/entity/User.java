package com.example.security.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String checkrole;
	@ManyToMany
	@JoinTable(
			name = "role_user",
			joinColumns = @JoinColumn(name ="id"),
			inverseJoinColumns = @JoinColumn(name ="roleid")
			)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<Role> roles = new HashSet<Role>();
	
}
