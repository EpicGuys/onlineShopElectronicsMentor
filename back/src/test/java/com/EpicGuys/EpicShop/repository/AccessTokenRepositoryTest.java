package com.EpicGuys.EpicShop.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.EpicGuys.EpicShop.entity.AccessToken;
import com.EpicGuys.EpicShop.jpa.AccessTokenRepository;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AccessTokenRepositoryTest {
	@Autowired
	private AccessTokenRepository accessTokenRepository;
	
	private final String email = "lolo@mail.ru";
	private final String token = "token";
	private final Long expirationTime = 1000000L;
	
	@Test
	public void Save_ShouldReturnSavedAccessToken() {
		AccessToken accessToken = AccessToken.builder()
										.email(email)
										.accessToken(token)
										.expirationTime(expirationTime)
										.build();

		AccessToken savedAccessToken = accessTokenRepository.save(accessToken);
		
		Assertions.assertNotNull(savedAccessToken);
		Assertions.assertEquals(email, savedAccessToken.getEmail());
	}
	
	@Test
	public void getById_ShouldDeleteAccessToken() {
		AccessToken accessToken = AccessToken.builder()
				.email(email)
				.accessToken(token)
				.expirationTime(expirationTime)
				.build();
		
		accessTokenRepository.save(accessToken);
		AccessToken savedAccessToken = accessTokenRepository.getById(email);
		
		Assertions.assertNotNull(savedAccessToken);
		Assertions.assertEquals(email, savedAccessToken.getEmail());
	}
	
	@Test
	public void deleteAllByExpirationTimeBefore_ShouldDeleteAllExpiredAccessTokens() {
		AccessToken accessTokenExpired = AccessToken.builder()
				.email(email)
				.accessToken(token)
				.expirationTime(expirationTime)
				.build();
		AccessToken accessTokenNotExpired = AccessToken.builder()
				.email(email)
				.accessToken(token)
				.expirationTime(expirationTime + 1000L)
				.build();
		
		accessTokenRepository.save(accessTokenExpired);
		accessTokenRepository.save(accessTokenExpired);
		Integer removedTokens = accessTokenRepository.deleteAllByExpirationTimeBefore(expirationTime + 100L);
		
		Assertions.assertEquals(1, removedTokens);
	}
}
