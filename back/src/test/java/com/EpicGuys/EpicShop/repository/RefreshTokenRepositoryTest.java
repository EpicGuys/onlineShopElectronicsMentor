package com.EpicGuys.EpicShop.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.EpicGuys.EpicShop.entity.AccessToken;
import com.EpicGuys.EpicShop.entity.RefreshToken;
import com.EpicGuys.EpicShop.jpa.AccessTokenRepository;
import com.EpicGuys.EpicShop.jpa.RefreshTokenRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RefreshTokenRepositoryTest {
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	private final String email = "lolo@mail.ru";
	private final String token = "token";
	private final Long expirationTime = 1000000L;
	
	@Test
	public void Save_ShouldReturnSavedRefreshToken() {
		RefreshToken refreshToken = RefreshToken.builder()
										.email(email)
										.refreshToken(token)
										.expirationTime(expirationTime)
										.build();

		RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
		
		Assertions.assertNotNull(savedRefreshToken);
		Assertions.assertEquals(email, savedRefreshToken.getEmail());
	}
	
	@Test
	public void getById_ShouldDeleteRefreshToken() {
		RefreshToken refreshToken = RefreshToken.builder()
				.email(email)
				.refreshToken(token)
				.expirationTime(expirationTime)
				.build();
		
		refreshTokenRepository.save(refreshToken);
		RefreshToken savedRefreshToken = refreshTokenRepository.getById(email);
		
		Assertions.assertNotNull(savedRefreshToken);
		Assertions.assertEquals(email, savedRefreshToken.getEmail());
	}
	
	@Test
	public void deleteAllByExpirationTimeBefore_ShouldDeleteAllExpiredAccessTokens() {
		RefreshToken refreshTokenExpired = RefreshToken.builder()
				.email(email)
				.refreshToken(token)
				.expirationTime(expirationTime)
				.build();
		RefreshToken refreshTokenNotExpired = RefreshToken.builder()
				.email(email)
				.refreshToken(token)
				.expirationTime(expirationTime + 1000L)
				.build();
		
		refreshTokenRepository.save(refreshTokenExpired);
		refreshTokenRepository.save(refreshTokenExpired);
		Integer removedTokens = refreshTokenRepository.deleteAllByExpirationTimeBefore(expirationTime + 100L);
		
		Assertions.assertEquals(1, removedTokens);
	}
}
