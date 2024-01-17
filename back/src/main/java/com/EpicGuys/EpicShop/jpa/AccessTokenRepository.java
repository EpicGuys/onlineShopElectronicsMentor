package com.EpicGuys.EpicShop.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EpicGuys.EpicShop.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, String>{
	//public Optional<AccessToken> getAccessTokenByEmail(String email);
	//public void deleteByEmail(String email);
}
