package com.EpicGuys.EpicShop.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EpicGuys.EpicShop.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> getUserByEmail(String email);
}
