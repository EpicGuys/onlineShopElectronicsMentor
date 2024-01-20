package com.EpicGuys.EpicShop.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.EpicGuys.EpicShop.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
	public int deleteAllByExpirationTimeBefore(Long currentTime);
	//public Optional<RefreshToken> getRefreshTokenByEmail(String email);
	//public void deleteByEmail(String email);
}
