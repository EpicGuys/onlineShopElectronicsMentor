package com.EpicGuys.EpicShop.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.EpicGuys.EpicShop.entity.RefreshToken;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>{
	//public Optional<RefreshToken> getRefreshTokenByEmail(String email);
	//public void deleteByEmail(String email);
}
