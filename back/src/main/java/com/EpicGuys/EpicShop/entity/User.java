package com.EpicGuys.EpicShop.entity;

import java.util.Set;

import com.epicGuys.app.articles.entity.Role;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "_user")
public class User {
	@Id
	@GeneratedValue
	private Long id;
	
	private String email;
	
	private String password;
	
	@OneToOne(mappedBy = "user")
	private Person userData;
	
	@ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
	@CollectionTable(joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	private Set<Role> roles;
}
